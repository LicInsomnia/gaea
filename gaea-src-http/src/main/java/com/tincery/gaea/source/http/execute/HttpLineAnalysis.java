package com.tincery.gaea.source.http.execute;


import com.tincery.gaea.api.base.HttpMeta;
import com.tincery.gaea.api.src.HttpData;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.source.http.constant.HttpConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.DatatypeConverter;
import java.util.*;




@Component
public class HttpLineAnalysis implements SrcLineAnalysis<HttpData> {


    @Autowired
    private HttpLineSupport httpLineSupport;

    /**
     * 将一行（一个）数据打包  数据传递格式为byte数组 切分  用 prefix（subName） + 特殊分隔符 + suffix（content）组成
     */
    @Override
    public HttpData pack(String line) throws Exception {
        HttpData httpMetaData = new HttpData();
        String[] split = line.split(HttpConstant.HTTP_CONSTANT);
        String subName = split[0];
        String text;
        if (split.length < 2) {
            text = "";
        } else {
            text = split[1];
        }
        byte[] value = text.getBytes();

        handlePrefix(httpMetaData,subName);

        if (value.length < 4 || !isLegalHeader(value)) {
            httpMetaData.setDataType(-1);
            int copyLength = Math.min(20, value.length);
            byte[] payloadMeta = new byte[copyLength];
            System.arraycopy(value, 0, payloadMeta, 0, copyLength);
            String payload = DatatypeConverter.printHexBinary(payloadMeta);
            if (httpMetaData.getIsResponse()){
                httpLineSupport.setMalformedPayload(null, payload, httpMetaData);
            }else {
                httpLineSupport.setMalformedPayload(payload, null, httpMetaData);
            }
        }else{
            handleSuffix(httpMetaData,subName,text);
        }

        return httpMetaData;
    }

    /**
     * 处理截取出来的subName 前缀的方法
     * @param httpData 要封装属性的对象
     * @param subName 截取的字符串
     */
    private void handlePrefix(HttpData httpData,String subName){
        setSubName(subName,httpData);
        fixHttpData(httpData,subName);
    }

    /**
     * 处理截取出来的后缀（Content） 的方法
     *
     * @param httpData 要封装属性的对象
     * @param subName  截取的字符串
     * @param text     content 字符串
     */
    private void handleSuffix(HttpData httpData, String subName, String text) throws Exception {
        List<String> reqs = flagResponse(text);
        int blank = 0;
        for (int i = 0; i < reqs.size(); i++) {
            String req = reqs.get(i);
            if (req.isEmpty()) {
                blank++;
                continue;
            }
            String textError = fixSuffixData(httpData, req, i - blank);
            if (null != textError) {
                //TODO 输出错误日志
                throw new Exception(textError + ":\n" + text + "\n" + req + "\n");
            }
        }
    }

    /**
     * 判断字节数组是否符合规范
     * @param bytes 判断的数组
     * @return boolean
     */
    private boolean isLegalHeader(byte[] bytes) {
        byte[] header = new byte[4];
        System.arraycopy(bytes, 0, header, 0, 4);
        String head = new String(header);
        return HttpConstant.legelHeader.contains(head);
    }

    private void setSubName(String subName,HttpData httpData){
        httpData.setSubName(subName.substring(0, subName.lastIndexOf(StringUtils.DEFAULT_SEP)));
    }


    /**
     * 如果不在outputmap中 补充httpData的属性
     * 0.syn 1.fin 2.captime_n 3. endtime_n 4.uppkt 5.upbyte 6.downpkt 7.downbyte
     *  8.clientIp 9.serverIp
     * 10. serverPort 11.clientPort 12.source 13.targetName
     * 14.imsi 15.imei 16.msisdn
     * 17 clientIpOuter,
     * 18 serverIpOuter,
     * 19 clientPortOuter,
     * 20 serverPortOuter,
     * 21 protocolOuter,
     * 22. userId
     * 23. serverId
     * 24.outformMac
     * 25.isResponse
     * @param httpData 一条数据
     */
    private void fixHttpData(HttpData httpData, String subName) {
        String[] element = subName.split(StringUtils.DEFAULT_SEP, -1);
        httpData.setSyn(SourceFieldUtils.parseBooleanStr(element[0]));
        httpData.setFin(SourceFieldUtils.parseBooleanStr(element[1]));
        long captimeN = Long.parseLong(element[2]);
        httpData.setCapTime(DateUtils.validateTime(captimeN));
        long endTimeN = Long.parseLong(element[3]);
        httpData.setDuration(endTimeN - captimeN);
        this.httpLineSupport.set7Tuple(null,
                null,
                element[8],
                element[9],
                element[10],
                element[11],
                "6",
                HeadConst.PRONAME.HTTP,
                httpData
        );
        this.httpLineSupport.setFlow(element[4],
                element[5],
                element[6],
                element[7],
                httpData
        );
        httpData.setServerIpInfo(this.httpLineSupport.getLocation(httpData.getServerIp()));
        httpData.setClientIpInfo(this.httpLineSupport.getLocation(httpData.getClientIp()));
        httpData.setSource(element[12]);
        this.httpLineSupport.setTargetName(element[13], httpData);
        this.httpLineSupport.setGroupName(httpData);
        httpData.setImsi(element[14])
                .setImei(element[15])
                .setMsisdn(element[16]);
        this.httpLineSupport.set5TupleOuter(element[17], element[18], element[19], element[20], element[21], httpData);
        httpData.setUserId(element[22])
                .setServerId(element[23]);
        httpData.setIsResponse("1".equals(element[25].substring(0, 1)));
        httpData.setKey(subName.substring(0, subName.lastIndexOf(StringUtils.DEFAULT_SEP)));
//        httpData.reMarkTargetName(userId2TargetName);
        this.httpLineSupport.isForeign(httpData.getServerIp());
    }


    /**
     * 用来对http的正文进行标记，从而便于后面进行分割。
     * 适用于型似"POST xxxx"和"POST\n xxxx"的http
     * PUT HEAD DELETE OPTIONS GET POST HTTP/1.1
     * @param http http全文
     *             //     * @param flag 后文将用于分割的标志位
     */
    static List<String> flagResponse(String http) {
        String flag = "\07\08\09";
        String[] splited = http.replace("POST /", flag + "POST /").
                replace("HTTP/1.1 2", flag + "HTTP/1.1 2").
                replace("HTTP/1.1 1", flag + "HTTP/1.1 1").
                replace("HTTP/1.0 2", flag + "HTTP/1.0 2").
                replace("HTTP/1.0 1", flag + "HTTP/1.0 1").
                replace("GET /", flag + "GET /").
                replace("HTTP/1.1\n2", flag + "HTTP/1.1 2").
                replace("HTTP/1.1\n1", flag + "HTTP/1.1 1").
                replace("HTTP/1.0\n2", flag + "HTTP/1.0 2").
                replace("HTTP/1.0\n1", flag + "HTTP/1.0 1").
                replace("POST\n/", flag + "POST /").
                replace("GET\n/", flag + "GET /").
                split(flag);
        return Arrays.asList(splited);
    }

    /**
     * 为截取的content封装属性
     *
     * @param httpData httpData
     * @param text     content数据
     * @param index    循环的角标
     * @return 错误信息
     */
    private String fixSuffixData(HttpData httpData, String text, Integer index) {
        HttpMeta meta;
        if (CollectionUtils.isEmpty(httpData.getMetas())) {
            httpData.setMetas(new ArrayList<>());
        }
        if (httpData.getMetas().size() > index) {
            meta = httpData.getMetas().get(index);
            meta.setHasResponse(httpData.getIsResponse());
        } else {
            meta = new HttpMeta();
            meta.setHasResponse(httpData.getIsResponse());
            httpData.getMetas().add(meta);
        }
        //meta = new HttpMeta(httpData.getIsResponse());
        //httpData.metas.add(meta);
        if (text.length() < 4 || !getLegelHeader().contains(text.substring(0, 4))) {
            meta.setContent(text, httpData.getIsResponse());
            meta.addMethod("", false);
        } else {
            meta.isMalformed = false;
            String headers = text.split("\r\n\r\n")[0];
            String[] lines = headers.split("\n");
            String method = lines[0].split(" ")[0].trim();
            //boolean getIsResponse() = method.startsWith("HTTP/");
            meta.setContent(text, httpData.getIsResponse());
            meta.addMethod(method, !httpData.getIsResponse());
            httpData.setDataType(1);
            if (!httpData.getIsResponse()) {
                meta.setUrl(lines[0].substring(method.length() + 1)
                        .replace(" HTTP/1.1", "").trim());
            }
            for (String line : lines) {
                if (!line.contains(":")) {
                    continue;
                }
                String key = line.split(":")[0];
                String value = line.substring(key.length() + 1).trim();
                key = key.toLowerCase();
                switch (key) {
                    case "host":
                        meta.setHost(value);
                        break;
                    case "content-length":
                        if (httpData.getIsResponse()) {
                            meta.responseContentLength = value.isEmpty() ? 0 : Integer.parseInt(value);
                        } else {
                            meta.requestContentLength = value.isEmpty() ? 0 : Integer.parseInt(value);
                        }
                        //meta.contentLength = value.isEmpty() ? 0 : Integer.parseInt(value);
                        break;
                    case "content-type":
                        meta.contentType = value;
                        break;
                    case "user-agent":
                        meta.userAgent = value;
                        break;
                    case "accept-language":
                        meta.acceptLanguage = value;
                        break;
                    case "from":
                        meta.from = value;
                        break;
                    case "authorization":
                        meta.authorization = value.length() > 0;
                        break;
                    case "proxy-authenticate":
                        meta.proxyauth = value.length() > 0;
                        break;
                    case "accept-encoding":
                        meta.acceptEncoding = value;
                        break;
                    default:
                        if (httpData.getIsResponse()) {
                            meta.setResponseHeaders(key, value);
                        } else {
                            meta.setRequestHeaders(key, value);
                        }
                        //meta.setHeaders(key, value);
                        break;
                }
            }
        }
        return null;
    }

    private Set<String> getLegelHeader(){
        return new HashSet<String>() {{
            add("GET ");
            add("POST");
            add("PUT ");
            add("HEAD");
            add("DELE");
            add("OPTI");
            add("HTTP");
        }};
    }
}
