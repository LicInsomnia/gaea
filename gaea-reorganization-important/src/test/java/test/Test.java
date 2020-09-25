package test;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.api.src.EmailData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class Test {
    static List<EmailData> emailDataList = new ArrayList<>();


    public static void main(String[] args) throws IOException {
       /* EmailData emailData =  new EmailData();
        emailData.setSource("这是我草");
        emailData.setDownByte(234L);
        emailData.setRcpt(Arrays.asList("a","b","c"));*/
       // JSONObject s = ((JSONObject) JSONObject.toJSON(emailData));
        String a = "{\"emailPath\":\"/home/email/email/20200917/controlWarnEml/5ce901e5513a61d32a01d6a393625e33_2770.eml\",\"transceiverRelationship\":[{\"Cc\":\"\",\"sender\":\"2651708847@qq.com<2651708847@qq.com>\",\"recipient\":\"13716097002<13716097002@163.com>\",\"secretDelivery\":\"\"}],\"matchFlag\":3,\"sendDate\":1596704798000,\"subject\":\"Fw: Fengyali2\",\"remark\":\"\",\"imsi\":\"\",\"unencryptAnnexTotalNum\":0,\"textHtml\":\"/home/email/output/emailAttach/20200917/controlWarnEml/5ce901e5513a61d32a01d6a393625e33_2770___0.txt\",\"password\":\"pdcjvftbirjyeabh\",\"Mail-X-Password\":\"pdcjvftbirjyeabh\",\"X-Mailer\":\"Foxmail 7.2.16.188[cn]\",\"cellphone\":0,\"text\":\"2651708847@qq.com From: 2651708847@qq.comDate: 2020-08-06 17:01To: 13716097002Subject: FengyaliCESHIFENGYALI2651708847@qq.com\",\"encryptAnnexTotalNum\":0,\"dataSourceIp\":\"192.168.1.94(发)局域网  对方和您在同一内部网\",\"unknownAnnexTotalNum\":0,\"workStatus\":\"0\",\"realIpAddress\":\"局域网  对方和您在同一内部网\",\"X-Originating-IPConvert\":3232235870,\"annexTotalNum\":0,\"received\":\"\",\"userId\":\"\",\"uploadDate\":1600322497000,\"X-Originating-IP\":\"192.168.1.94\",\"name\":\"5ce901e5513a61d32a01d6a393625e33_2770.eml\",\"Delivered-To\":\"\",\"username\":\"2651708847@qq.com\"}\n";
        File aaa = new File("emaiasdf.json");
        FileOutputStream fileOutputStream = new FileOutputStream(aaa);
        for (int i = 0; i < 10000; i++) {
            JSONObject parse = (JSONObject)JSONObject.parse(a);
            parse.put("id", UUID.randomUUID().toString());
            String b =  (parse.toJSONString()+"\n");
            System.out.println(b);
            byte[] bytes = b.getBytes();
            fileOutputStream.write(bytes);
        }
        fileOutputStream.close();
    }
   /* @Benchmark
    @Warmup(iterations = 1, time = 3)
    @Fork(5)
    @BenchmarkMode(Mode.Throughput)
    @Measurement(iterations = 1, time = 3)*/
    public void aa() throws IllegalAccessException {
    }
}
