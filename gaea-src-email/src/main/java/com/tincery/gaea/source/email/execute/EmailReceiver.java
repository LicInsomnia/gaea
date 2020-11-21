package com.tincery.gaea.source.email.execute;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.tincery.gaea.api.src.EmailData;
import com.tincery.gaea.api.src.email.*;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.rule.AlarmRule;
import com.tincery.gaea.core.base.rule.PassRule;
import com.tincery.gaea.core.base.rule.Rule;
import com.tincery.gaea.core.base.rule.RuleRegistry;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.src.AbstractSrcReceiver;
import com.tincery.gaea.core.src.SrcProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author gxz
 */

@Component
@Slf4j
@Setter
@Getter
public class EmailReceiver extends AbstractSrcReceiver<EmailData> {


    private boolean emailSuffixAlarm;

    @Autowired
    private AlarmRule alarmRule;
    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private PassRule passRule;
    @Resource(name = "sysMongoTemplate")
    private MongoTemplate sysMongoTemplate;

    private CopyOnWriteArrayList<EmailData> emailList = new CopyOnWriteArrayList<>();

    @Autowired
    public void setAnalysis(EmailLineAnalysis analysis) {
        this.analysis = analysis;
    }


    @Override
    @Autowired
    public void setProperties(SrcProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getHead() {
        return HeadConst.EMAIL_HEADER;
    }

    @Override
    public void receive(TextMessage textMessage) throws JMSException {
        log.info("消息传递时间：{}；执行时间：{}", DateUtils.format(textMessage.getJMSTimestamp()), DateUtils.now());
        String text = textMessage.getText();
        ZipFile file;
        try {
            file = new ZipFile(text);
            log.info("开始解析压缩文件:[{}]", file.getName());
            long startTime = System.currentTimeMillis();
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(text)),StandardCharsets.UTF_8);
            ZipEntry zipEntry;
            while((zipEntry = zipInputStream.getNextEntry()) != null){
                analysisFile(zipEntry,file);
            }
            long l = Instant.now().toEpochMilli();
            zipInputStream.close();
            this.clearFile(new File(file.getName()));
            System.out.println("clearFile用了"+(l-Instant.now().toEpochMilli()));
            this.free();
            System.out.println("free用了"+(l-Instant.now().toEpochMilli()));
            log.info("文件:[{}]处理完成，用时{}毫秒", file.getName(), (System.currentTimeMillis() - startTime));
        } catch (IOException | MessagingException e) {
            throw new JMSException("压缩文件错误");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析文件  文件有两种格式 eml(0-n)  bcp(1)
     * @param zipEntry 压缩文件
     */
    private void analysisFile(ZipEntry zipEntry,ZipFile file) throws Exception {

        List<String> lines = getLines(zipEntry,file);
        if (lines.isEmpty()) {
            return;
        }
        int executor = this.properties.getExecutor();
        if (executor <= 1 || executor <= lines.size()) {
            analysisLine(lines);
        } else {
            List<List<String>> partitions = Lists.partition(lines, (lines.size() / executor) + 1);
            CountDownLatch countDownLatch = new CountDownLatch(partitions.size());
            for (List<String> partition : partitions) {
                executorService.execute(() -> {
                    try {
                        analysisLine(partition);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("解析实体时出现特殊异常");
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解析一个文件返回多条数据
     * @param zipEntry 文件
     * @return 数据
     */
    private List<String> getLines(ZipEntry zipEntry,ZipFile zipFile) throws Exception {
        ArrayList<String> result = new ArrayList<>();
        if(zipEntry.toString().endsWith("bcp")){
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(zipFile.getInputStream(zipEntry)));
            String line;
            while((line = br.readLine()) != null){
                result.add(line);
            }
            br.close();
        }else if (zipEntry.toString().endsWith("eml")){
            EmailData email = parseEmail(zipFile,zipEntry);
            emailList.add(email);
        }else {
            throw new IllegalArgumentException("文件结尾不正确");
        }
        return result;
    }

    /**
     * 转变email为json
     * @param zipFile 压缩文件
     * @return 数据
     */
    private EmailData parseEmail(ZipFile zipFile,ZipEntry zipEntry) throws Exception {
        Properties props = new Properties();
//        Authenticator authenticator = new Authenticator(){};
        Session session = Session.getDefaultInstance(props, null);
        InputStream inMsg = zipFile.getInputStream(zipEntry);
        MimeMessage msg = new MimeMessage(session, inMsg);
        EmailData emailData = new EmailData();
        fixEmailContent(emailData,msg ,zipEntry);
        return emailData;
    }

    /**
     * 填装email内容
     * @param emailData 要封装的数据
     * @param msg email信息
     */
    private void fixEmailContent(EmailData emailData, MimeMessage msg , ZipEntry zipEntry) throws Exception {

        /*依次装载emailData的几个附属属性*/
        //①1.装载认证属性
        EmailAuth emailAuth = new EmailAuth();
        fixEmailAuth(emailAuth,msg);
        //②1.装载邮件关系属性
        EmailTransceiverRelationship relationship = new EmailTransceiverRelationship();
        fixEmailRelationShip(relationship,msg);
        //②3.装载邮件附件属性
        EmailAnnexDetail emailAnnexDetail = new EmailAnnexDetail();
        fixEmailAnnex(emailAnnexDetail,msg);
        //②4.装载邮件要素信息属性
        EmailPart emailPart = new EmailPart();
        fixEmailPart(emailPart,msg,zipEntry);
        emailPart.setAnnexDetail(emailAnnexDetail);
        emailPart.setTransceiverRelationship(relationship);
        //③1.装载邮件拓展属性
        EmailExtension extension = new EmailExtension();
        fixEmailExtension(extension,msg);

        emailData.setAuth(emailAuth)
                 .setPart(emailPart)
                 .setExtension(extension);
    }

    /**
     * 装载邮件要素信息
     * @param emailPart 实体
     * @param msg 数据
     *            TODO 部分要素信息不明确
     */
    private void fixEmailPart(EmailPart emailPart, Message msg , ZipEntry zipEntry) {

        emailPart.setFileName(zipEntry.getName());
        //是否满足布控预警？不明确 emailPart.setMatchFlag(0);
        //当前时间  不明确 Header： Date头  和Received头中都有时间 还是解读压缩包的时间？
        //emailPart.setUploadDate()
    }

    /**
     * 装载邮件拓展信息
     * @param extension 拓展信息
     * @param msg 数据
     */
    private void fixEmailExtension(EmailExtension extension, Message msg) {
    }

    /**
     * 装载邮件附件属性
     * @param emailAnnexDetail 附件
     * @param msg 数据
     *            TODO 部分附件属性不明确
     */
    private void fixEmailAnnex(EmailAnnexDetail emailAnnexDetail, MimeMessage msg) throws Exception {
        // getContent() 是获取包裹内容, Part相当于外包装

        Object o = msg.getContent();
        if (o instanceof Multipart) {
            Multipart multipart = (Multipart) o;
            reMultipart(multipart,emailAnnexDetail);
        } else if (o instanceof Part) {
            Part part = (Part) o;
            rePart(part,emailAnnexDetail);
        } else {
            System.out.println("类型" + msg.getContentType());
//            map.put("type", msg.getContentType());
            System.out.println("内容" + msg.getContent());
//            map.put("content", msg.getContent());
        }
    }

    /**
     * 装载邮件关系属性
     * @param relationship 实体
     * @param msg 数据
     */
    private void fixEmailRelationShip(EmailTransceiverRelationship relationship, MimeMessage msg) throws MessagingException {
        Address[] to = msg.getRecipients(Message.RecipientType.TO);
        if (ArrayUtil.isNotEmpty(to)){
            for (Address allRecipient : to) {
                relationship.fixRecipient(((InternetAddress)allRecipient).getPersonal());
            }
        }

        Address[] from = msg.getFrom();
        if (ArrayUtil.isNotEmpty(from)){
            for (Address address : from) {
                relationship.fixSender(((InternetAddress)address).getPersonal());
            }
        }

        Address[] cc = msg.getRecipients(Message.RecipientType.CC);
        if (ArrayUtil.isNotEmpty(cc)){
            for (Address address : cc) {
                relationship.fixCc(((InternetAddress)address).getPersonal());
            }
        }

        Address[] bcc = msg.getRecipients(Message.RecipientType.BCC);
        if (ArrayUtil.isNotEmpty(bcc)){
            for (Address address : bcc) {
                relationship.fixSecretDelivery(((InternetAddress)address).getPersonal());
            }
        }

        relationship.fixMailAddrAndDomain();
    }

    /**
     * 装载邮件认证属性
     * @param emailAuth 实体
     * @param msg 数据
     */
    private void fixEmailAuth(EmailAuth emailAuth, MimeMessage msg) throws MessagingException {
        String[] userName = msg.getHeader("Mail-X-Username");
        if (userName != null){
            emailAuth.setUserName(userName[0]);
        }
        String[] password = msg.getHeader("Mail-X-Password");
        if (password != null){
            emailAuth.setPassword(password[0]);
        }
    }

    /**
     * 解析内容
     *
     * @param part
     * @throws Exception
     */
    private static void rePart(Part part,EmailAnnexDetail annexDetail) throws Exception {
        if (part.getDisposition() != null) {
            String strFileNmae = part.getFileName();
            if(strFileNmae != null) {
                /*到这里 内容是附件*/
                // MimeUtility.decodeText解决附件名乱码问题
                strFileNmae = MimeUtility.decodeText(strFileNmae);
                String strFile = "D:\\gaeaData\\" + strFileNmae;

                System.out.println("发现附件: "+ strFileNmae);
                // 打开附件的输入流
                FileOutputStream out = bakAnnex(strFile, part);
                annexDetail.fixAnnexSize(out.getChannel().size());
                out.close();

                annexDetail.fixAnnexName(strFileNmae);
                annexDetail.fixAnnexCharset(part.getHeader("Content-Transfer-Encoding"));
                annexDetail.fixAnnexPath(strFile);
                //TODO 附件类型是什么东西 以下是contentType 和 后缀(.rar什么的)
                String contentType = part.getContentType();
                String[] split = strFileNmae.split("\\.");
                String suffix = split[split.length-1];

                //是否加密

            }
            System.out.println("内容类型: "+ MimeUtility.decodeText(part.getContentType()));
            System.out.println("附件内容:" + part.getContent());
        } else {
            /*到这里 内容是文本*/
            if (part.getContentType().startsWith("text/plain")) {
                System.out.println("文本内容：" + part.getContent());
            } else {
                // System.out.println("HTML内容：" + part.getContent());
            }
        }
    }

    /**
     * 保存附件
     * @param strFile 保存目录
     * @param part 包裹
     */
    private static FileOutputStream bakAnnex(String strFile, Part part) throws IOException, MessagingException {
        InputStream in = part.getInputStream();
        FileOutputStream out = new FileOutputStream(strFile);
        byte[] bytes = new byte[1024];
        while(in.read(bytes,0,1024) != -1){
            out.write(bytes);
        }
        in.close();
        return out;
    }

    /**
     * 接卸包裹（含所有邮件内容(包裹+正文+附件)）
     * @param multipart
     * @throws Exception
     */
    private static void reMultipart(Multipart multipart,EmailAnnexDetail annexDetail) throws Exception {
        // System.out.println("邮件共有" + multipart.getCount() + "部分组成");
        // 依次处理各个部分
        for (int j = 0, n = multipart.getCount(); j < n; j++) {
            // System.out.println("处理第" + j + "部分");
            Part part = multipart.getBodyPart(j);// 解包, 取出 MultiPart的各个部分,
            // 每部分可能是邮件内容,
            // 也可能是另一个小包裹(MultipPart)
            // 判断此包裹内容是不是一个小包裹, 一般这一部分是 正文 Content-Type: multipart/alternative
            if (part.getContent() instanceof Multipart) {
                Multipart p = (Multipart) part.getContent();// 转成小包裹
                // 递归迭代
                reMultipart(p,annexDetail);
            } else {
                rePart(part,annexDetail);
            }
        }
    }

    @Override
    public void init() {
        // loadGroup();
        registryRules(passRule);
        registryRules(alarmRule);
        CommonConfig.EmailConfig emailInfo = commonConfig.getEmail();
    }

    public void registryRules(Rule rule) {
        RuleRegistry.getInstance().putRule(rule);
    }


}
