package com.tincery.gaea.source.http.execute;


import com.tincery.gaea.api.base.HttpMeta;
import com.tincery.gaea.api.src.HttpData;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.source.http.constant.HttpConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Component
public class HttpLineAnalysis implements SrcLineAnalysis<HttpData> {


    @Autowired
    private HttpLineSupport httpLineSupport;

    /**
     * 用来对http的正文进行标记，从而便于后面进行分割。
     * 适用于型似"POST xxxx"和"POST\n xxxx"的http
     * PUT HEAD DELETE OPTIONS GET POST HTTP/1.1
     *
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
     * 将一行（一个）数据打包  数据传递格式为byte数组 切分  用 prefix（subName） + 特殊分隔符 + suffix（content）组成
     */
    @Override
    public HttpData pack(String line) throws Exception {
        HttpData httpMetaData = new HttpData();
        /*
         数据格式  key + 苍叔牛逼 + index + 苍叔牛逼  + value
         以下。subName 为要装载的key  以key区分meta
         text为content  装载meta
         index为meta的顺序
         */
        String[] split = line.split(HttpConstant.HTTP_CONSTANT);
        String subName = split[0];
        String text = getContentText(split);
        byte[] value = text.getBytes(StandardCharsets.ISO_8859_1);
        /* 设置httpData的key  和 common */
        handlePrefix(httpMetaData, subName);
        if (value.length < 4 || !isLegalHeader(value)) {
            fixMalformed(httpMetaData,value);
        } else {
            /*填装meta*/
            handleSuffix(httpMetaData, subName, text, Integer.valueOf(split[1]));
        }
        httpMetaData.setForeign(httpLineSupport.isForeign(httpMetaData.getServerIp()));
        httpMetaData.setServerLocation(this.httpLineSupport.getLocation(httpMetaData.getServerIp()));
        httpMetaData.setClientLocation(this.httpLineSupport.getLocation(httpMetaData.getClientIp()));
        return httpMetaData;
    }

    /**
     * 填装malformed数据
     * @param httpMetaData 实体
     * @param value 源
     */
    private void fixMalformed(HttpData httpMetaData, byte[] value) {
        httpMetaData.setDataType(-1);
        int copyLength = Math.min(20, value.length);
        byte[] payloadMeta = new byte[copyLength];
        System.arraycopy(value, 0, payloadMeta, 0, copyLength);
        String payload = DatatypeConverter.printHexBinary(payloadMeta);
        if (httpMetaData.getIsResponse()) {
            this.httpLineSupport.setMalformedPayload(null, payload, httpMetaData);
        } else {
            this.httpLineSupport.setMalformedPayload(payload, null, httpMetaData);
        }
    }

    /**
     * 获得content数据
     * @param split 源
     * @return content
     */
    private String getContentText(String[] split) {
        if (split.length < 3) {
            return "";
        } else {
            return split[2];
        }
    }

    /**
     * 处理截取出来的subName 前缀的方法
     * @param httpData 要封装属性的对象
     * @param subName  截取的字符串
     */
    private void handlePrefix(HttpData httpData, String subName) {
        setSubName(subName, httpData);
        fixHttpData(httpData, subName);
    }

    /**
     * 处理截取出来的后缀（Content） 的方法
     *
     * @param httpData 要封装属性的对象
     * @param subName  截取的字符串
     * @param text     content 字符串
     */
    private void handleSuffix(HttpData httpData, String subName, String text, Integer sort) throws Exception {
        List<String> reqs = flagResponse(text);
        int blank = 0;
        for (int i = 0; i < reqs.size(); i++) {
            String req = reqs.get(i);
            if (req.isEmpty()) {
                blank++;
                continue;
            }
            String textError = fixSuffixData(httpData, req, i - blank, sort);
            if (null != textError) {
                //TODO 输出错误日志
//                throw new Exception(textError + ":\n" + subName + "\n" + req + "\n");
            }
        }
    }

    /**
     * 判断字节数组是否符合规范
     *
     * @param bytes 判断的数组
     * @return boolean
     */
    private boolean isLegalHeader(byte[] bytes) {
        byte[] header = new byte[4];
        System.arraycopy(bytes, 0, header, 0, 4);
        String head = new String(header, StandardCharsets.ISO_8859_1);
        return HttpConstant.legelHeader.contains(head);
    }

    private void setSubName(String subName, HttpData httpData) {
        httpData.setSubName(subName.substring(0, subName.lastIndexOf(StringUtils.DEFAULT_SEP)));
    }

    /**
     * 如果不在outputmap中 补充httpData的属性
     * 0.syn 1.fin 2.captime_n 3. endtime_n 4.uppkt 5.upbyte 6.downpkt 7.downbyte
     * 8.clientIp 9.serverIp
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
     *
     * @param httpData 一条数据
     */
    private void fixHttpData(HttpData httpData, String subName) {
        String[] elements = subName.split(StringUtils.DEFAULT_SEP, -1);
        this.httpLineSupport.setSynAndFin(elements[0],elements[1],httpData);
        this.httpLineSupport.setTime(Long.parseLong(elements[2]), Long.parseLong(elements[3]), httpData);
        this.httpLineSupport.set7Tuple(null,
                null,
                elements[8],
                elements[9],
                elements[10],
                elements[11],
                "6",
                HeadConst.PRONAME.HTTP,
                httpData
        );
        this.httpLineSupport.setFlow(elements[4],
                elements[5],
                elements[6],
                elements[7],
                httpData
        );

        httpData.setSource(elements[12]);
        this.httpLineSupport.setTargetName(elements[13], httpData);
        this.httpLineSupport.setGroupName(httpData);
        this.httpLineSupport.setMobileElements(elements[14],elements[15],elements[16],httpData);
        this.httpLineSupport.set5TupleOuter(elements[17], elements[18], elements[19], elements[20], elements[21], httpData);
        this.httpLineSupport.setPartiesId(elements[22],elements[23],httpData);
        if (StringUtils.isNotEmpty(elements[25]) && elements[25].length()>=1){
            httpData.setIsResponse("1".equals(elements[25].substring(0, 1)));
        }
        httpData.setKey(subName.substring(0, subName.lastIndexOf(StringUtils.DEFAULT_SEP)));
        this.httpLineSupport.isForeign(httpData.getServerIp());
    }

    /**
     * 为截取的content封装属性
     *
     * @param httpData httpData
     * @param text     content数据
     * @param index    循环的角标
     * @param sort     在文件的位置
     * @return 错误信息
     */
    private String fixSuffixData(HttpData httpData, String text, Integer index, Integer sort) {
        HttpMeta meta = getHttpMeta(httpData, index);
        meta.setHasResponse(httpData.getIsResponse());
        if (text.length() < 4 || !getLegelHeader().contains(text.substring(0, 4))) {
            meta.setContent(text, httpData.getIsResponse());
            meta.addMethod("", false);
        } else {
            meta.isMalformed = false;
            String[] lines = getContentLines(text);
            String method = lines[0].split(" ")[0].trim();
            meta.setContent(text, httpData.getIsResponse());
            meta.addMethod(method, !httpData.getIsResponse());
            httpData.setDataType(1);
            if (!httpData.getIsResponse()) {
                meta.setUrl(lines[0].substring(method.length() + 1)
                        .replace(" HTTP/1.1", "").trim());
            }
            fixHttpMeta(meta,lines,httpData.getIsResponse());
        }
        meta.setIndex(sort);
        httpData.getMetas().add(meta);
        return null;
    }

    /**
     * 获得httpMeta以便装载
     * @param httpData 实体
     * @param index 实体顺序
     * @return httpMeta实体
     */
    private HttpMeta getHttpMeta(HttpData httpData, Integer index) {
        HttpMeta meta;
        if (CollectionUtils.isEmpty(httpData.getMetas())) {
            httpData.setMetas(new ArrayList<>());
        }
        if (httpData.getMetas().size() > index) {
            meta = httpData.getMetas().get(index);
        } else {
            meta = new HttpMeta();
        }
        return meta;
    }

    /**
     * 获得所有content行
     * @param text 元数据
     * @return content行数据
     */
    private String[] getContentLines(String text) {
        String headers = text.split("\r\n\r\n")[0];
        return headers.split("\n");
    }

    /**
     * 装填HttpMeta
     * @param meta 要装填的实体
     * @param lines 数据
     */
    private void fixHttpMeta(HttpMeta meta, String[] lines, boolean isResponse) {
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
                    int length = value.isEmpty() ? 0 : Integer.parseInt(value);
                    if (isResponse) {
                        meta.setResponseContentLength(length);
                    } else {
                        meta.setRequestContentLength(length);
                    }
                    //meta.contentLength = value.isEmpty() ? 0 : Integer.parseInt(value);
                    break;
                case "content-type":
                    meta.setContentType(value);
                    break;
                case "user-agent":
                    meta.setUserAgent(value);
                    break;
                case "accept-language":
                    meta.setAcceptLanguage(value);
                    break;
                case "from":
                    meta.setFrom(value);
                    break;
                case "authorization":
                    meta.setAuthorization(value.length() > 0);
                    break;
                case "proxy-authenticate":
                    meta.setProxyauth(value.length() > 0);
                    break;
                case "accept-encoding":
                    meta.setAcceptEncoding(value);
                    break;
                default:
                    if (isResponse) {
                        meta.setResponseHeaders(key, value);
                    } else {
                        meta.setRequestHeaders(key, value);
                    }
                    //meta.setHeaders(key, value);
                    break;
            }
        }
    }

    private Set<String> getLegelHeader() {
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
