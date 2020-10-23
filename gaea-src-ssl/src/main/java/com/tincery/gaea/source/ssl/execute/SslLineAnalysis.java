package com.tincery.gaea.source.ssl.execute;


import com.tincery.gaea.api.src.SslData;
import com.tincery.gaea.core.base.tool.util.DateUtils;
import com.tincery.gaea.core.base.tool.util.StringUtils;
import com.tincery.gaea.core.src.SrcLineAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gxz
 */

@Component
@Slf4j
public class SslLineAnalysis implements SrcLineAnalysis<SslData> {

    @Autowired
    private SslLineSupport sslLineSupport;

    /**
     * 0.syn            1.fin           2.startTime         3.endTime           4.uppkt
     * 5.upbyte         6.downpkt       7.downbyte          8.datatype(1)       9.protocol
     * 10.serverMac     11.clientMac    12.serverIp_n       13.clientIp_n       14.serverPort
     * 15.clientPort    16.source       17.runleName        18.imsi             19.imei
     * 20.msisdn        21.outclientip  22.outserverip      23.outclientport    24.outserverport
     * 25.outproto      26.userid       27.serverid         28.ismac2outer      29.data1 / upPayload
     * 30.data2 / downPayload           ...dataN
     */
    @Override
    public SslData pack(String line) {
        SslData sslData = new SslData();
        String[] elements = StringUtils.FileLineSplit(line);
        sslData.setSource(elements[16]);
        sslData.setCapTime(DateUtils.validateTime(Long.parseUnsignedLong(elements[2])));
        sslData.setDurationTime((Long.parseUnsignedLong(elements[3])) - Long.parseUnsignedLong(elements[2]));
        sslData.setImsi(elements[18]);
        sslData.setImei(elements[19]);
        sslData.setMsisdn(elements[20]);
        sslData.setDataType(Integer.parseInt(elements[8]));
        sslData.setSyn("1".equals(elements[0]));
        sslData.setFin("1".equals(elements[1]));
        this.sslLineSupport.set7Tuple(
                elements[10], elements[11], elements[12],
                elements[13], elements[14], elements[15],
                elements[9], "SSL", sslData);
        this.sslLineSupport.setFlow(
                elements[4], elements[5], elements[6],
                elements[7], sslData);
        this.sslLineSupport.setTargetName(elements[17], sslData);
        this.sslLineSupport.setGroupName(sslData);
        this.sslLineSupport.set5TupleOuter(elements[21], elements[22], elements[23], elements[24], elements[25], sslData);
        sslData.setUserId(elements[26]);
        sslData.setServerId(elements[27]);
        if (sslData.getDataType() == -1) {
            this.sslLineSupport.setMalformedPayload(elements[29], elements[30], sslData);
        } else {
            if (elements[29].contains("malformed")) {
                sslData.setDataType(-2);
            } else {
//                addSslExtension(elements, sslData);
            }
        }
        return sslData;
    }

//    private void addSslExtension(String[] elements, SslData sslDaa) {
//        List<String> handshake = new ArrayList<>();
//        StringBuilder handShake = new StringBuilder(256);
//        boolean isServerCer = false;
//        if (handShake.length() > 5) {
//            handShake.setLength(handShake.length() - 5);
//            sslData.setHandshake(handShake.toString());
//        }
//        for (int i = 29; i < elements.length; i++) {
//            if (elements[i].isEmpty()) {
//                continue;
//            }
//            sslData.setDataType(0);
//            if (!elements[i].startsWith("(")) {
//                handShake.append(elements[i]).append(" --> ");
//            } else {
//                String kv = elements[i].substring(1, elements[i].length() - 1);
//                if (kv.startsWith("CipherSuite:")) {
//                    sslData.setClientCipherSuite(kv.substring(12).trim());
//                } else if (kv.startsWith("ServerName:")) {
//                    sslData.setServerName(kv.substring(11).trim());
//                } else if (kv.startsWith("Random:")) {
//                    sslData.setRandom(kv.substring(7).trim());
//                } else if (kv.startsWith("ssl_version:")) {
//                    addVersion(kv.substring(12).trim(), sslData);
//                } else if (kv.startsWith("cipher_suite:")) {
//                    addCipherSuites(kv.substring(13).toLowerCase(), sslData);
//                } else if (kv.startsWith("cert:")) {
//                    addCerchain(kv.substring(5).trim(), sslData);
//                }
//            }
//            if ("Certificate".equals(elements[i])) {
//                isServerCer = !isServerCer;
//            }
//            if ("Certificate Request".equalsIgnoreCase(elements[i])) {
//                sslData.setDoubleSession(true);
//            }
//        }
//    }
//
//
//    public void addVersion(String version, SslData sslData) {
//        if (StringUtils.isNotEmpty(version)) {
//            List<String> versions = sslData.getVersions();
//            if (null == versions) {
//                versions = new ArrayList<>();
//                sslData.setVersions(versions);
//            }
//            versions.add(version);
//        }
//    }
//
//    public void addCipherSuites(String cipherSuite, SslData sslData) {
//        if (StringUtils.isNotEmpty(cipherSuite)) {
//            List<String> cipherSuites = sslData.getCipherSuites();
//            if (null == cipherSuites) {
//                cipherSuites = new ArrayList<>();
//                sslData.setVersions(cipherSuites);
//            }
//            cipherSuites.add(cipherSuite);
//        }
//    }
//
//    private void addCerchain(String cer, SslData sslData) {
//        if (StringUtils.isNotEmpty(cer)) {
//            List<String> cerChain = sslData.getCerChain();
//            if (null == cerChain) {
//                cerChain = new ArrayList<>();
//                sslData.setCerChain(cerChain);
//            }
//            cerChain.add(cer);
//        }
//    }

}
