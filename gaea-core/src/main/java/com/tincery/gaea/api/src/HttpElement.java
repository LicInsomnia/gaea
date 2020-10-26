package com.tincery.gaea.api.src;

import com.alibaba.fastjson.annotation.JSONField;
import com.tincery.gaea.api.base.HttpMeta;
import com.tincery.gaea.api.base.Location;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HttpElement extends AbstractSrcData {
    /**
     * 用来保存上下行数据
     */
    private String contentLength;

    private Boolean isResponse;
    private Location serverLocation;
    private Location clientLocation;
    private String host;
    private String method;
    private String urlRoot;
    private String userAgent;
    private String payload;
    public String url;
    public String sld;
    public String tld;
    public Integer requestContentLength;
    public Integer responseContentLength;
    public String contentType;
    public String acceptLanguage;
    public String from;
    public Boolean authorization;
    public Boolean proxyauth;
    public List<Map<String, String>> reqHeaders;
    public List<Map<String, String>> repHeaders;
    public String acceptEncoding;
    public String params;
    @JSONField(serialize = false)
    public Boolean hasResponse;
    @JSONField(serialize = false)
    public Boolean isMalformed;
    @JSONField(serialize = false)
    public String request;
    public String response;
    public String content;

    public Boolean isTrash;


    public void init(HttpData httpData, HttpMeta httpMeta){
        this.setSource(httpData.getSource())
                .setCapTime(httpData.getCapTime())
                .setUserId(httpData.getUserId())
                .setServerId(httpData.getServerId())
                .setProName(httpData.getProName())
                .setProtocol(httpData.getProtocol())
                .setClientMac(httpData.getClientMac())
                .setServerMac(httpData.getServerMac())
                .setClientIp(httpData.getClientIp())
                .setServerIp(httpData.getServerIp())
                .setClientPort(httpData.getClientPort())
                .setServerPort(httpData.getServerPort())
                .setClientIpOuter(httpData.getClientIpOuter())
                .setServerIpOuter(httpData.getServerIpOuter())
                .setClientPortOuter(httpData.getClientPortOuter())
                .setServerPortOuter(httpData.getServerPortOuter())
                .setProtocolOuter(httpData.getProtocolOuter())
//                .setImp(httpData.getImp())
                .setMalformedUpPayload(httpData.getMalformedUpPayload())
                .setMalformedDownPayload(httpData.getMalformedDownPayload())
                .setImsi(SourceFieldUtils.parseStringStrEmptyToNull(httpData.getImsi()))
                .setImei(SourceFieldUtils.parseStringStrEmptyToNull(httpData.getImei()))
                .setMsisdn(SourceFieldUtils.parseStringStrEmptyToNull(httpData.getMsisdn()))
//                .setDataType(httpData.getDataType())
                .setGroupName(httpData.getGroupName())
                .setTargetName(httpData.getTargetName())
                .setUpPkt(httpData.getUpPkt())
                .setUpByte(httpData.getUpByte())
                .setDownPkt(httpData.getDownPkt())
                .setDownByte(httpData.getDownByte())
                .setDuration(httpData.getDuration())
//                .setSyn(httpData.getSyn())
//                .setFin(httpData.getFin())
                .setForeign(httpData.getForeign())
                .setCaseTags(httpData.getCaseTags());

        this.setMacOuter(httpData.getMacOuter())
                .setExtension(httpData.getExtension());
//                .setCompleteSession(httpData.getCompleteSession());


        this.setUrl(httpMeta.getUrl())
                .setHost(httpMeta.getHost())
                .setMethod(httpMeta.getMethod().toString())
                .setSld(httpMeta.getSld())
                .setTld(httpMeta.getTld())
                .setRequestContentLength(httpMeta.getRequestContentLength())
                .setResponseContentLength(httpMeta.getResponseContentLength())
                .setContentType(httpMeta.getContentType())
                .setUserAgent(httpMeta.getUserAgent())
                .setAcceptLanguage(httpMeta.getAcceptLanguage())
                .setFrom(httpMeta.getFrom())
                .setAuthorization(httpMeta.getAuthorization())
                .setProxyauth(httpMeta.getProxyauth())
                .setReqHeaders(httpMeta.getReqHeaders())
                .setRepHeaders(httpMeta.getRepHeaders())
                .setAcceptEncoding(httpMeta.getAcceptEncoding())
                .setUrlRoot(httpMeta.getUrlRoot())
                .setParams(httpMeta.getParams())
                .setHasResponse(httpMeta.hasResponse)
                .setIsMalformed(httpMeta.isMalformed)
                .setRequest(httpMeta.getRequest())
                .setResponse(httpMeta.getResponse());

        this.setClientLocation(httpData.getClientLocation())
                .setServerLocation(httpData.getServerLocation());

        if (CollectionUtils.isEmpty(httpData.getCaseTags())){
            this.setIsTrash(false);
        }else {
            this.setIsTrash(httpData.getCaseTags().contains("垃圾"));
        }
        this.setContent(httpMeta.getContent());

    }
}
