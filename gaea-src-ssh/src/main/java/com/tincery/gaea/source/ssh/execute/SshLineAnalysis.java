package com.tincery.gaea.source.ssh.execute;


import com.tincery.gaea.api.src.SshData;
import com.tincery.gaea.api.src.extension.SshExtension;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.tool.util.SourceFieldUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import com.tincery.gaea.core.src.SrcLineSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        SshData sshData = new SshData();
        String[] elements = StringUtils.FileLineSplit(line);
        setFixProperties(elements, sshData);
        SshExtension sshExtension = new SshExtension();
        if (sshData.getDataType() == -1) {
            srcLineSupport.setMalformedPayload(elements[29], elements[30], sshData);
        } else {
            setExtension(elements, sshExtension);
        }
        sshData.setSshExtension(sshExtension);
        return sshData;
    }


    private void setFixProperties(String[] elements, SshData sshData) {
        this.srcLineSupport.set7Tuple(elements[10],
                elements[11],
                elements[12],
                elements[13],
                elements[14],
                elements[15],
                elements[9],
                // proName 赋默认值  如果匹配到了相关application 会替换掉proName
                HeadConst.PRONAME.SSH,
                sshData
        );
        this.srcLineSupport.setFlow(elements[4], elements[5], elements[6], elements[7], sshData);
        sshData.setDataType(Integer.parseInt(elements[8]));
        sshData.setSyn(SourceFieldUtils.parseBooleanStr(elements[0]))
                .setFin(SourceFieldUtils.parseBooleanStr(elements[1]))
                .setSource(elements[16]);
        this.srcLineSupport.setTargetName(elements[17], sshData);
        this.srcLineSupport.setGroupName(sshData);
        this.srcLineSupport.setTime(Long.parseLong(elements[2]),Long.parseLong(elements[3]),sshData);
        sshData.setImsi(elements[18])
                .setImei(elements[19])
                .setMsisdn(elements[20]);
        sshData.setUserId(elements[26])
                .setServerId(elements[27]);
        sshData.setForeign(this.srcLineSupport.isForeign(sshData.getServerIp()));
        srcLineSupport.set5TupleOuter(elements[21], elements[22], elements[23], elements[24], elements[25], sshData);
    }

    private void setExtension(String[] element, SshExtension sshExtension) {
        Boolean isClient = null;
        Map<String, String> extension = new HashMap<>();
        List<String> messageList = new ArrayList<>();
        String serverProtocol = "";
        String clientProtocol = "";
        for (int i = 29; i < element.length; i++) {
            String msg = element[i];
            if (msg.isEmpty()) {
                continue;
            }
            if (msg.startsWith("Server:Protocol ")) {
                serverProtocol = msg.substring(16);
                isClient = false;
            } else if (msg.startsWith("Client:Protocol ")) {
                clientProtocol = msg.substring(16);
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
                String fieldArray = extension.getOrDefault(prefix + kv[0], "");
                /*特殊处理 将kv1中的，全部换成；*/
                if (StringUtils.isNotEmpty(kv[1])) {
                    kv[1] = kv[1].replace(",", ";");
                }
                fieldArray += kv[1];
                extension.put(prefix + kv[0], fieldArray);
            }
        }
        sshExtension.setMessageList(messageList);
        sshExtension.setServerProtocol(serverProtocol);
        sshExtension.setClientProtocol(clientProtocol);
        fixExtensionData(extension, sshExtension);
    }

    /**
     * 这里的extension 是拓展属性（因为ssh的拓展属性被拆开了，所以不需要直接set到sshData上）
     * 根据extension取值  为sshData添加拓展属性
     *
     * @param extension    拓展属性
     * @param sshExtension 源数据
     */
    private void fixExtensionData(Map<String, String> extension, SshExtension sshExtension) {
        String c_kex_algorithms = extension.getOrDefault("c_kex_algorithms", null);
        String s_kex_algorithms = extension.getOrDefault("s_kex_algorithms", null);
        sshExtension.setClientKexAlgorithms(c_kex_algorithms)
                .setServerKexAlgorithms(s_kex_algorithms)
                .setFinalKexAlgorithms(getFinalAlgorithm(c_kex_algorithms, s_kex_algorithms));
        String c_encryption_algorithms_server_to_client = extension.getOrDefault("c_encryption_algorithms_server_to_client", null);
        String s_encryption_algorithms_server_to_client = extension.getOrDefault("s_encryption_algorithms_server_to_client", null);
        sshExtension.setClientEncryptionAlgorithmsServerToClient(c_encryption_algorithms_server_to_client)
                .setServerEncryptionAlgorithmsServerToClient(s_encryption_algorithms_server_to_client)
                .setFinalEncryptionAlgorithmsServerToClient(getFinalAlgorithm(c_encryption_algorithms_server_to_client, s_encryption_algorithms_server_to_client));
        String c_encryption_algorithms_client_to_server = extension.getOrDefault("c_encryption_algorithms_client_to_server", null);
        String s_encryption_algorithms_client_to_server = extension.getOrDefault("s_encryption_algorithms_client_to_server", null);
        sshExtension.setClientEncryptionAlgorithmsClientToServer(c_encryption_algorithms_client_to_server)
                .setServerEncryptionAlgorithmsClientToServer(s_encryption_algorithms_client_to_server)
                .setFinalEncryptionAlgorithmsClientToServer(getFinalAlgorithm(c_encryption_algorithms_client_to_server, s_encryption_algorithms_client_to_server));
        String c_mac_algorithms_client_to_server = extension.getOrDefault("c_mac_algorithms_client_to_server", null);
        String s_mac_algorithms_client_to_server = extension.getOrDefault("s_mac_algorithms_client_to_server", null);
        sshExtension.setClientMacAlgorithmsClientToServer(c_mac_algorithms_client_to_server)
                .setServerMacAlgorithmsClientToServer(s_mac_algorithms_client_to_server)
                .setFinalMacAlgorithmsClientToServer(getFinalAlgorithm(c_mac_algorithms_client_to_server, s_mac_algorithms_client_to_server));
        String c_mac_algorithms_server_to_client = extension.getOrDefault("c_mac_algorithms_server_to_client", null);
        String s_mac_algorithms_server_to_client = extension.getOrDefault("s_mac_algorithms_server_to_client", null);
        sshExtension.setClientMacAlgorithmsServerToClient(c_mac_algorithms_server_to_client)
                .setServerMacAlgorithmsServerToClient(s_mac_algorithms_server_to_client)
                .setFinalMacAlgorithmsServerToClient(getFinalAlgorithm(c_mac_algorithms_server_to_client, s_mac_algorithms_server_to_client));
        String c_server_host_key_algorithms = extension.getOrDefault("c_server_host_key_algorithms", null);
        String s_server_host_key_algorithms = extension.getOrDefault("s_server_host_key_algorithms", null);
        sshExtension.setClientServerHostKeyAlgorithms(c_server_host_key_algorithms)
                .setServerServerHostKeyAlgorithms(s_server_host_key_algorithms)
                .setFinalServerHostKeyAlgorithms(getFinalAlgorithm(c_server_host_key_algorithms, s_server_host_key_algorithms));
        String c_compression_algorithms_client_to_server = extension.getOrDefault("c_compression_algorithms_client_to_server", null);
        String s_compression_algorithms_client_to_server = extension.getOrDefault("s_compression_algorithms_client_to_server", null);
        sshExtension.setClientCompressionAlgorithmsClientToServer(c_compression_algorithms_client_to_server)
                .setServerCompressionAlgorithmsClientToServer(s_compression_algorithms_client_to_server)
                .setFinalCompressionAlgorithmsClientToServer(getFinalAlgorithm(c_compression_algorithms_client_to_server, s_compression_algorithms_client_to_server));
        String c_compression_algorithms_server_to_client = extension.getOrDefault("c_compression_algorithms_server_to_client", null);
        String s_compression_algorithms_server_to_client = extension.getOrDefault("s_compression_algorithms_server_to_client", null);
        sshExtension.setClientCompressionAlgorithmsServerToClient(c_compression_algorithms_server_to_client)
                .setServerCompressionAlgorithmsServerToClient(s_compression_algorithms_server_to_client)
                .setFinalCompressionAlgorithmsServerToClient(getFinalAlgorithm(c_compression_algorithms_server_to_client, s_compression_algorithms_server_to_client));
        String c_publicKey_dh_e = extension.getOrDefault("c_publicKey_dh_e", null);
        String s_publicKey_df_f = extension.getOrDefault("s_publicKey_df_f", null);
        sshExtension.setClientPublicKey(c_publicKey_dh_e)
                .setServerPublicKey(s_publicKey_df_f)
                .setFinalPublicKeyAlgorithms(getFinalAlgorithm(c_publicKey_dh_e,s_publicKey_df_f));
    }


    private String getFinalAlgorithm(String clientAlgorithmStr, String serverAlgorithmStr) {
        String finalAlgorithm = "";
        if (StringUtils.isEmpty(clientAlgorithmStr) || StringUtils.isEmpty(serverAlgorithmStr)) {
            return null;
        }
        String[] clientAlgorithmArray = clientAlgorithmStr.split(";");
        for (String clientItem : clientAlgorithmArray) {
            if (serverAlgorithmStr.contains(clientItem)) {
                finalAlgorithm = clientItem;
                return finalAlgorithm;
            }
        }
        finalAlgorithm = "no match";
        return finalAlgorithm;
    }

}
