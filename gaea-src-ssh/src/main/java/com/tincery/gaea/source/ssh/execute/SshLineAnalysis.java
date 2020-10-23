package com.tincery.gaea.source.ssh.execute;


import com.tincery.gaea.api.src.SshData;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import com.tincery.starter.base.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author gxz
 */

@Component
public class SshLineAnalysis implements SrcLineAnalysis<SshData> {


    @Autowired
    public SrcLineSupport srcLineSupport;

    /****
     *
     * 将一条记录包装成实体类
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
            srcLineSupport.setMalformedPayload(elements[29],elements[30],sshMetaData);
        }else{
            setExtension(elements,sshMetaData);
        }
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
        srcLineSupport.setTargetName(element[17], sshData);
        srcLineSupport.setGroupName(sshData);
        long captimeN = Long.parseLong(element[2]);
        sshData.setCapTime(DateUtils.validateTime(captimeN));
        long endTimeN = Long.parseLong(element[3]);
        sshData.setDuration(endTimeN - captimeN);
        sshData.setImsi(element[18])
                .setImei(element[19])
                .setMsisdn(element[20]);
        sshData.setUserId(element[26])
                .setServerId(element[27]);
        sshData.setForeign(this.srcLineSupport.isForeign(sshData.getServerIp()));
    }

    private void setExtension(String[] element,SshData sshData){
        Boolean isClient = null;
        Map<String, List<String>> extension = new HashMap<>();
        List<String> messageList = new ArrayList<>();
        List<String> serverProtocol = new ArrayList<>();
        List<String> clientProtocol = new ArrayList<>();
        for (int i = 29; i < element.length; i++) {
            String msg = element[i];
            if (msg.isEmpty()) {
                continue;
            }
            if (msg.startsWith("Server:Protocol ")) {
                serverProtocol.add(msg.substring(16));
                isClient = false;
            } else if (msg.startsWith("Client:Protocol ")) {
                clientProtocol.add(msg.substring(16));
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
                List<String> fieldArray = extension.getOrDefault(prefix + kv[0], new ArrayList<>());
                /*特殊处理 将kv1中的，全部换成；*/
                if (StringUtils.isNotEmpty(kv[1])){
                    kv[1] = kv[1].replace(",",";");
                }
                fieldArray.add(kv[1]);
                extension.put(prefix+kv[0],fieldArray);
            }
        }
        sshData.setMessageList(messageList);
        sshData.setServerProtocol(serverProtocol);
        sshData.setClientProtocol(clientProtocol);
        fixExtensionData(extension,sshData);
    }

    /**
     * 这里的extension 是拓展属性（因为ssh的拓展属性被拆开了，所以不需要直接set到sshData上）
     * 根据extension取值  为sshData添加拓展属性
     * @param extension 拓展属性
     * @param sshData 源数据
     */
    private void fixExtensionData(Map<String, List<String>> extension,SshData sshData){
        List<String> c_kex_algorithms = extension.getOrDefault("c_kex_algorithms", new ArrayList<>());
        List<String> s_kex_algorithms = extension.getOrDefault("s_kex_algorithms", new ArrayList<>());
        sshData.setClientKexAlgorithms(c_kex_algorithms)
                .setServerKexAlgorithms(s_kex_algorithms)
                .setFinalKexAlgorithms(getFinalAlgorithm(c_kex_algorithms,s_kex_algorithms));
        List<String> c_encryption_algorithms_server_to_client = extension.getOrDefault("c_encryption_algorithms_server_to_client", new ArrayList<>());
        List<String> s_encryption_algorithms_server_to_client = extension.getOrDefault("s_encryption_algorithms_server_to_client", new ArrayList<>());
        sshData.setClientEncryptionAlgorithmsServerToClient(c_encryption_algorithms_server_to_client)
                .setServerEncryptionAlgorithmsServerToClient(s_encryption_algorithms_server_to_client)
                .setFinalEncryptionAlgorithmsServerToClient(getFinalAlgorithm(c_encryption_algorithms_server_to_client,s_encryption_algorithms_server_to_client));
        List<String> c_encryption_algorithms_client_to_server = extension.getOrDefault("c_encryption_algorithms_client_to_server", new ArrayList<>());
        List<String> s_encryption_algorithms_client_to_server = extension.getOrDefault("s_encryption_algorithms_client_to_server", new ArrayList<>());
        sshData.setClientEncryptionAlgorithmsClientToServer(c_encryption_algorithms_client_to_server)
                .setServerEncryptionAlgorithmsClientToServer(s_encryption_algorithms_client_to_server)
                .setFinalEncryptionAlgorithmsClientToServer(getFinalAlgorithm(c_encryption_algorithms_client_to_server,s_encryption_algorithms_client_to_server));
        List<String> c_mac_algorithms_client_to_server = extension.getOrDefault("c_mac_algorithms_client_to_server", new ArrayList<>());
        List<String> s_mac_algorithms_client_to_server = extension.getOrDefault("s_mac_algorithms_client_to_server", new ArrayList<>());
        sshData.setClientMacAlgorithmsClientToServer(c_mac_algorithms_client_to_server)
                .setServerMacAlgorithmsClientToServer(s_mac_algorithms_client_to_server)
                .setFinalMacAlgorithmsClientToServer(getFinalAlgorithm(c_mac_algorithms_client_to_server,s_mac_algorithms_client_to_server));
        List<String> c_mac_algorithms_server_to_client = extension.getOrDefault("c_mac_algorithms_server_to_client", new ArrayList<>());
        List<String> s_mac_algorithms_server_to_client = extension.getOrDefault("s_mac_algorithms_server_to_client", new ArrayList<>());
        sshData.setClientMacAlgorithmsServerToClient(c_mac_algorithms_server_to_client)
                .setServerMacAlgorithmsServerToClient(s_mac_algorithms_server_to_client)
                .setFinalMacAlgorithmsServerToClient(getFinalAlgorithm(c_mac_algorithms_server_to_client,s_mac_algorithms_server_to_client));
        List<String> c_server_host_key_algorithms = extension.getOrDefault("c_server_host_key_algorithms", new ArrayList<>());
        List<String> s_server_host_key_algorithms = extension.getOrDefault("s_server_host_key_algorithms", new ArrayList<>());
        sshData.setClientServerHostKeyAlgorithms(c_server_host_key_algorithms)
                .setServerServerHostKeyAlgorithms(s_server_host_key_algorithms)
                .setFinalServerHostKeyAlgorithms(getFinalAlgorithm(c_server_host_key_algorithms,s_server_host_key_algorithms));
        List<String> c_compression_algorithms_client_to_server = extension.getOrDefault("c_compression_algorithms_client_to_server", new ArrayList<>());
        List<String> s_compression_algorithms_client_to_server = extension.getOrDefault("s_compression_algorithms_client_to_server", new ArrayList<>());
        sshData.setClientCompressionAlgorithmsClientToServer(c_compression_algorithms_client_to_server)
                .setServerCompressionAlgorithmsClientToServer(s_compression_algorithms_client_to_server)
                .setFinalCompressionAlgorithmsClientToServer(getFinalAlgorithm(c_compression_algorithms_client_to_server,s_compression_algorithms_client_to_server));
        List<String> c_compression_algorithms_server_to_client = extension.getOrDefault("c_compression_algorithms_server_to_client", new ArrayList<>());
        List<String> s_compression_algorithms_server_to_client = extension.getOrDefault("s_compression_algorithms_server_to_client", new ArrayList<>());
        sshData.setClientCompressionAlgorithmsServerToClient(c_compression_algorithms_server_to_client)
                .setServerCompressionAlgorithmsServerToClient(s_compression_algorithms_server_to_client)
                .setFinalCompressionAlgorithmsServerToClient(getFinalAlgorithm(c_compression_algorithms_server_to_client,s_compression_algorithms_server_to_client));
    }


    private List<String> getFinalAlgorithm(List<String> clientAlgorithmStr, List<String> serverAlgorithmStr) {
        ArrayList<String> finalAlgorithm = new ArrayList<>();
        if (CollectionUtils.isEmpty(clientAlgorithmStr) || CollectionUtils.isEmpty(serverAlgorithmStr)){
            finalAlgorithm.add("no match");
            return finalAlgorithm;
        }
        String clientAlgorithm = clientAlgorithmStr.get(0);
        String serverAlgorithm = serverAlgorithmStr.get(0);
        String[] clientAlgorithmArray = clientAlgorithm.split(";");
        for (String clientItem : clientAlgorithmArray) {
            if (serverAlgorithm.contains(clientItem)){
                finalAlgorithm.add(clientItem);
                return finalAlgorithm;
            }
        }
        finalAlgorithm.add("no match");
        return finalAlgorithm;
    }




}
