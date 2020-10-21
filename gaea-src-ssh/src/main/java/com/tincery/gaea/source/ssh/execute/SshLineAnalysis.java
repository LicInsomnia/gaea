package com.tincery.gaea.source.ssh.execute;


import com.tincery.gaea.api.src.SshData;
import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.IpChecker;
import com.tincery.gaea.core.base.component.support.PayloadDetector;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import com.tincery.starter.base.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * @author gxz
 */

@Component
public class SshLineAnalysis implements SrcLineAnalysis<SshData> {


    @Autowired
    public SrcLineSupport srcLineSupport;

    @Autowired
    public IpChecker ipChecker;

    /****
     *
     * 将一条记录包装成实体类
     *
     * 0.syn      1.fin             2.startTime      3.endTime
     * 4.uppkt      5.upbyte     6.downpkt   7.downbyte
     * 8.datatype(+-1) 9.protocol        10.serverMac            11.clientMac          12.serverIp_n
     * 13.clientIp_n          14.serverPort          15.clientPort         16.source
     * 17.ruleName       18.imsi           19. upPayload(datatype=-1)  20.downPayLoad(datatype=-1)
     *
     *  0.syn 1.fin 2.startTime 3.endTime 4.uppkt 5.upbyte
     *  6.downpkt 7.downbyte 8.datatype(+-1) 9.protocol 10. serverMac
     *  11.clientMac 12.serverIp_n 13.clientIp_n 14.serverPort 15.clientPort 16.source
     *  17.runleName 18.imsi 19.imei 20.msisdn 21.outclientip 22.outserverip 23.outclientport
     *  24.outserverport 25.outproto 26.userid 27.serverid 28.ismac2outer
     *  29.upPayload(data1：len)
     *  30.downPayload(data1:len)
     *  31. ...
     *  32.datan(datan:len)
     *  ↑括号内为datatype = 1时的数据
     *
     */
    @Override
    public SshData pack(String line) {
        SshData sshMetaData = new SshData();
        String[] elements = StringUtils.FileLineSplit(line);
        setFixProperties(elements, sshMetaData);
        // proName 赋默认值  如果匹配到了相关application 会替换掉proName
        sshMetaData.setProName("SSH");

        if (sshMetaData.getDataType() == -1){
            srcLineSupport.setMalformedPayload(elements[21],elements[22],sshMetaData);
        }else{
            setExtension(elements,sshMetaData);
        }
        setFinalAlgorithm(sshMetaData);
        srcLineSupport.set5TupleOuter(elements[21], elements[22], elements[23], elements[24], elements[25],sshMetaData);
        return sshMetaData;
    }



    private void setFixProperties(String[] element, SshData sshData) {
        sshData.setProtocol(Integer.parseInt(element[9]))
                .setServerMac(element[10])
                .setClientMac(element[11])
                .setServerIp(NetworkUtil.arrangeIp(element[12]))
                .setClientIp(NetworkUtil.arrangeIp(element[13]))
                .setServerPort(Integer.parseInt(element[14]))
                .setClientPort(Integer.parseInt(element[15]))
                .setUpPkt(Long.parseLong(element[4]))
                .setUpByte(Long.parseLong(element[5]))
                .setDownPkt(Long.parseLong(element[6]))
                .setDownByte(Long.parseLong(element[7]));
        sshData.setDataType(Integer.parseInt(element[8]));
        sshData.setSyn(SourceFieldUtils.parseBooleanStr(element[0]))
                .setFin(SourceFieldUtils.parseBooleanStr(element[1]));
        sshData.setSource(element[16]);
        srcLineSupport.setTargetName(element[17],sshData);
        srcLineSupport.setGroupName(sshData);
        long captimeN = Long.parseLong(element[2]);
        sshData.setCapTime(DateUtils.validateTime(captimeN));
        long endTimeN = Long.parseLong(element[3]);
        sshData.setDurationTime(captimeN-endTimeN);
        sshData.setImsi(element[18])
                .setImei(element[19])
                .setMsisdn(element[20]);
        sshData.setUserId(element[26])
            .setServerId(element[27]);

        sshData.setForeign(ipChecker.isForeign(sshData.getServerIp()));

    }

    private void setExtension(String[] element,SshData sshData){
        Boolean isClient = null;
        Map<String, Object> extension = new HashMap<>();
        List<String> messageList = new ArrayList<>();
        for (int i = 29; i < element.length; i++) {
            String msg = element[i];
            if (msg.isEmpty()) {
                continue;
            }
            if (msg.startsWith("Server:Protocol ")) {
                extension.put("s_protocol", msg.substring(16));
                isClient = false;
            } else if (msg.startsWith("Client:Protocol ")) {
                extension.put("c_protocol", msg.substring(16));
                isClient = true;
            } else if (msg.contains("(Message Code)")) {
                if (msg.startsWith("Server:")) {
                    isClient = false;
                } else if (msg.indexOf("Client:") == 0) {
                    isClient = true;
                }
                messageList.add(msg.replace("(Message Code)", ""));
            } else {
                if (null == isClient) {
                    continue;
                }
                String prefix = isClient ? "c_" : "s_";
                String[] kv = msg.split(":");
                if (kv.length != 2) {
                    continue;
                }
                extension.put(prefix + kv[0], kv[1]);
            }
        }
        sshData.setMessageList(messageList);
        sshData.setExtension(extension);
    }



    private void setFinalAlgorithm(SshData sshData){
        Map<String, Object> extension;
        if(Objects.nonNull(sshData.getExtension())){
            extension = sshData.getExtension();
        }else {
            extension = new HashMap<>();
        }
        String c_kex_algorithms = extension.getOrDefault("c_kex_algorithms", "").toString();
        String s_kex_algorithms = extension.getOrDefault("s_kex_algorithms", "").toString();
        extension.put("final_kex_algorithms", getFinalAlgorithm(c_kex_algorithms, s_kex_algorithms));
        String c_encryption_algorithms_server_to_client = extension.getOrDefault("c_encryption_algorithms_server_to_client", "").toString();
        String s_encryption_algorithms_server_to_client = extension.getOrDefault("s_encryption_algorithms_server_to_client", "").toString();
        extension.put("final_encryption_algorithms_server_to_client", getFinalAlgorithm(c_encryption_algorithms_server_to_client, s_encryption_algorithms_server_to_client));
        String c_encryption_algorithms_client_to_server = extension.getOrDefault("c_encryption_algorithms_client_to_server", "").toString();
        String s_encryption_algorithms_client_to_server = extension.getOrDefault("s_encryption_algorithms_client_to_server", "").toString();
        extension.put("final_encryption_algorithms_client_to_server", getFinalAlgorithm(c_encryption_algorithms_client_to_server, s_encryption_algorithms_client_to_server));
        String c_mac_algorithms_client_to_server = extension.getOrDefault("c_mac_algorithms_client_to_server", "").toString();
        String s_mac_algorithms_client_to_server = extension.getOrDefault("s_mac_algorithms_client_to_server", "").toString();
        extension.put("final_mac_algorithms_client_to_server", getFinalAlgorithm(c_mac_algorithms_client_to_server, s_mac_algorithms_client_to_server));
        String c_mac_algorithms_server_to_client = extension.getOrDefault("c_mac_algorithms_server_to_client", "").toString();
        String s_mac_algorithms_server_to_client = extension.getOrDefault("s_mac_algorithms_server_to_client", "").toString();
        extension.put("final_mac_algorithms_server_to_client", getFinalAlgorithm(c_mac_algorithms_server_to_client, s_mac_algorithms_server_to_client));
        String c_server_host_key_algorithms = extension.getOrDefault("c_server_host_key_algorithms", "").toString();
        String s_server_host_key_algorithms = extension.getOrDefault("s_server_host_key_algorithms", "").toString();
        extension.put("final_server_host_key_algorithms", getFinalAlgorithm(c_server_host_key_algorithms, s_server_host_key_algorithms));
        String c_compression_algorithms_client_to_server = extension.getOrDefault("c_compression_algorithms_client_to_server", "").toString();
        String s_compression_algorithms_client_to_server = extension.getOrDefault("s_compression_algorithms_client_to_server", "").toString();
        extension.put("final_compression_algorithms_client_to_server", getFinalAlgorithm(c_compression_algorithms_client_to_server, s_compression_algorithms_client_to_server));
        String c_compression_algorithms_server_to_client = extension.getOrDefault("c_compression_algorithms_server_to_client", "").toString();
        String s_compression_algorithms_server_to_client = extension.getOrDefault("s_compression_algorithms_server_to_client", "").toString();
        extension.put("final_compression_algorithms_server_to_client", getFinalAlgorithm(c_compression_algorithms_server_to_client, s_compression_algorithms_server_to_client));
        sshData.setExtension(extension);
    }

    private String getFinalAlgorithm(String clientAlgorithmStr, String serverAlgorithmStr) {
        String[] clientAlgorithms = clientAlgorithmStr.split(",");
        List<String> serverAlgorithms = Arrays.asList(serverAlgorithmStr.split(","));
        for (String clientAlgorithm : clientAlgorithms) {
            if (serverAlgorithms.contains(clientAlgorithm)) {
                return clientAlgorithm;
            }
        }
        return "no match";
    }



}
