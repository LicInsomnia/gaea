package com.tincery.gaea.core.dw;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.dw.AbstractDataWarehouseData;
import com.tincery.gaea.core.base.component.config.CommonConfig;
import com.tincery.gaea.core.base.component.config.NodeInfo;
import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.CerSelector;
import com.tincery.gaea.core.base.component.support.DnsRequest;
import com.tincery.gaea.core.base.component.support.IpSelector;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;

import java.util.*;
import java.util.stream.Collectors;

public class SessionFactory {

    private final IpSelector ipSelector;
    private final CerSelector cerSelector;
    private final ApplicationProtocol applicationProtocol;
    private final DnsRequest dnsRequest;
    private final Map<String, List<String>> extensionKeyMap;

    @SuppressWarnings("unchecked")
    public SessionFactory(IpSelector ipSelector, CerSelector cerSelector, ApplicationProtocol applicationProtocol, DnsRequest dnsRequest) {
        this.ipSelector = ipSelector;
        this.cerSelector = cerSelector;
        this.applicationProtocol = applicationProtocol;
        this.dnsRequest = dnsRequest;
        Map<String, Object> configs = (Map<String, Object>) CommonConfig.get(NodeInfo.getCategory());
        this.extensionKeyMap = (Map<String, List<String>>) configs.get("extensionkeys");
    }

    /**
     * 从csv数据中抽象
     *
     * @param category csv类型
     * @param csvRow   csv某一行{@link CsvRow}
     * @return 抽象是否成功
     */
    public AbstractDataWarehouseData create(String category, CsvRow csvRow) {
        AbstractDataWarehouseData abstractDataWarehouseData = appendBaseAndAttach(category, csvRow);
        List<String> extensionKeys = this.extensionKeyMap.get(category);
        switch (category) {
            case "session":
                this.append4Session(csvRow, abstractDataWarehouseData);
                break;
            case "ssl":
                this.append4Ssl(csvRow, extensionKeys, abstractDataWarehouseData);
                break;
            case "openvpn":
                this.append4OpenVpn(csvRow, extensionKeys, abstractDataWarehouseData);
                break;
            case "dns":
                this.append4Dns(csvRow, extensionKeys, abstractDataWarehouseData);
                break;
            case "http":
                this.append4Http(csvRow, extensionKeys, abstractDataWarehouseData);
                break;
            case "email":
                this.append4Email(csvRow, extensionKeys, abstractDataWarehouseData);
                break;
            case "isakmp":
                this.append4Isakmp(csvRow, extensionKeys, abstractDataWarehouseData);
                break;
            case "ssh":
                this.append4Ssh(csvRow, extensionKeys, abstractDataWarehouseData);
                break;
            case "ftp_telnet":
                this.append4FtpAndTelenet(csvRow, extensionKeys, abstractDataWarehouseData);
                break;
            case "esp_ah":
                this.append4EspAndAh(csvRow, extensionKeys, abstractDataWarehouseData);
                break;
            default:
                return null;
        }
        adjust(abstractDataWarehouseData);
        return abstractDataWarehouseData;
    }

    private AbstractDataWarehouseData appendBaseAndAttach(String category, CsvRow csvRow) {
        AbstractDataWarehouseData abstractDataWarehouseData = new AbstractDataWarehouseData();
        abstractDataWarehouseData.setTargetName(csvRow.get(HeadConst.CSV.TARGET_NAME))
                .setGroupName(csvRow.get(HeadConst.CSV.GROUP_NAME))
                .setUserId(csvRow.get(HeadConst.CSV.USER_ID))
                .setServerId(csvRow.get(HeadConst.CSV.SERVER_ID))
                .setSource(csvRow.get(HeadConst.CSV.SOURCE))
                .setCapTime(csvRow.getLong(HeadConst.CSV.CAPTIME_N))
                .setDurationTime(csvRow.getLongOrDefault(HeadConst.CSV.DURATION, 0L))
                .setProtocol(csvRow.getIntegerOrDefault(HeadConst.CSV.PROTOCOL, 0))
                .setProName(csvRow.get(HeadConst.CSV.PRONAME))
                .setClientMac(csvRow.get(HeadConst.CSV.CLIENT_MAC))
                .setServerMac(csvRow.get(HeadConst.CSV.SERVER_MAC))
                .setClientIp(csvRow.get(HeadConst.CSV.CLIENT_IP))
                .setServerIp(csvRow.get(HeadConst.CSV.SERVER_IP))
                .setClientPort(csvRow.getIntegerOrDefault(HeadConst.CSV.CLIENT_PORT, 0))
                .setServerPort(csvRow.getIntegerOrDefault(HeadConst.CSV.SERVER_PORT, 0))
                .setClientIpOuter(csvRow.get(HeadConst.CSV.CLIENT_IP_OUTER))
                .setServerIpOuter(csvRow.get(HeadConst.CSV.SERVER_IP_OUTER))
                .setClientPortOuter(csvRow.getIntegerOrDefault(HeadConst.CSV.CLIENT_PORT_OUTER, 0))
                .setServerPortOuter(csvRow.getIntegerOrDefault(HeadConst.CSV.SERVER_PORT_OUTER, 0))
                .setProtocolOuter(csvRow.getIntegerOrDefault(HeadConst.CSV.PROTOCOL_OUTER, 0))
                .setImsi(csvRow.get(HeadConst.CSV.IMSI))
                .setImei(csvRow.get(HeadConst.CSV.IMEI))
                .setMsisdn(csvRow.get(HeadConst.CSV.MSISDN))
                .setDataType(csvRow.getInteger(HeadConst.CSV.DATA_TYPE))
                .setUpPkt(csvRow.getLongOrDefault(HeadConst.CSV.UP_PKT, 0L))
                .setDownPkt(csvRow.getLongOrDefault(HeadConst.CSV.DOWN_PKT, 0L))
                .setUpByte(csvRow.getLongOrDefault(HeadConst.CSV.UP_BYTE, 0L))
                .setDownByte(csvRow.getLongOrDefault(HeadConst.CSV.DOWN_BYTE, 0L))
                .setServerPortOuter(csvRow.getIntegerOrDefault(HeadConst.CSV.SERVER_PORT, 0));
        abstractDataWarehouseData
                .setClientLocation(this.ipSelector.getCommonInformation(abstractDataWarehouseData.getClientIp()))
                .setServerLocation(this.ipSelector.getCommonInformation(abstractDataWarehouseData.getServerIp()))
                .setDataSource(category);
        String caseTags = csvRow.get(HeadConst.CSV.CASE_TAGS);
        if (null != caseTags) {
            abstractDataWarehouseData.setCaseTags(new HashSet<>(Arrays.asList(caseTags.split(";"))));
        }
        String id = Joiner.on("_").join(new Object[]{
                abstractDataWarehouseData.getTargetName(),
                abstractDataWarehouseData.getProtocol(),
                abstractDataWarehouseData.getClientIp(),
                abstractDataWarehouseData.getServerIp(),
                abstractDataWarehouseData.getClientPort(),
                abstractDataWarehouseData.getServerPort(),
                abstractDataWarehouseData.getCapTime()
        });
        return abstractDataWarehouseData.setId(id);
    }

    private void adjust(AbstractDataWarehouseData abstractDataWarehouseData) {
        // 设置dnsRequest
        abstractDataWarehouseData.setProtocolKnown(!HeadConst.PRONAME.OTHER.equals(abstractDataWarehouseData.getProName()));
        abstractDataWarehouseData.setMalFormed(Objects.equals(-1, abstractDataWarehouseData.getDataType()));
        if (!HeadConst.PRONAME.DNS.equals(abstractDataWarehouseData.getProName())) {
            String key = abstractDataWarehouseData.getUserId() + "_" + abstractDataWarehouseData.getServerIp();
            abstractDataWarehouseData.setDnsRequestBO(this.dnsRequest.getDnsRequest(key, abstractDataWarehouseData.getCapTime()));
            if (null == abstractDataWarehouseData.getKeyWord() && null != abstractDataWarehouseData.getDnsRequestBO()) {
                abstractDataWarehouseData.setKeyWord(abstractDataWarehouseData.getDnsRequestBO().getDomain());
            }
        }
        // 设置malformed
        if (!abstractDataWarehouseData.getMalFormed()) {
            abstractDataWarehouseData.setEnc(this.applicationProtocol.isEnc(abstractDataWarehouseData.getProName()));
        }
        // 设置tag
        if (abstractDataWarehouseData.getProtocol() == 6 && ((abstractDataWarehouseData.getUpByte() + abstractDataWarehouseData.getDownByte()) == 0)) {
            abstractDataWarehouseData.setTag("SYN");
            return;
        }
        /* 是否为netproc已解析协议 */
        if (null == abstractDataWarehouseData.getMalFormed()) {
            /* netproc未解析协议 */
            if (HeadConst.PRONAME.OTHER.equals(abstractDataWarehouseData.getProName())) {
                /* 特殊端口 */
                abstractDataWarehouseData.setTag("特殊端口");
            } else {
                /* 已知端口 */
                abstractDataWarehouseData.setTag(abstractDataWarehouseData.getProName().replace("(payload)", ""));
            }
        } else {
            /* netproc已解析协议 */
            if (abstractDataWarehouseData.getMalFormed()) {
                /* 异常协议 */
                if (HeadConst.PRONAME.OTHER.equals(abstractDataWarehouseData.getProName())) {
                    /* 未知异常协议 */
                    abstractDataWarehouseData.setTag("异常协议");
                } else {
                    /* 端口仿冒 */
                    abstractDataWarehouseData.setTag("端口仿冒");
                }
            } else {
                /* 正常协议 */
                abstractDataWarehouseData.setTag(abstractDataWarehouseData.getProName().replace("(payload)", ""));
            }
        }
    }

    private void putExtensionField(CsvRow csvRow, String key4Csv, String key2Ext, AbstractDataWarehouseData abstractDataWarehouseData) {
        String value = csvRow.get(key4Csv);
        if (null != value) {
            Map<String, Object> extension = abstractDataWarehouseData.getExtension();
            if (null == extension) {
                extension = new HashMap<>();
                abstractDataWarehouseData.setExtension(extension);
            }
            extension.put(key2Ext, value);
        }
    }

    private void putExtensionKeys(CsvRow csvRow, List<String> extensionKeys, AbstractDataWarehouseData abstractDataWarehouseData) {
        if (null == extensionKeys || extensionKeys.isEmpty()) {
            return;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = csvRow.getJsonObject(HeadConst.CSV.EXTENSION);
        } catch (Exception ignore) {
        }
        if (null != jsonObject) {
            Map<String, Object> extension = extensionKeys.stream()
                    .filter(jsonObject::containsKey)
                    .collect(Collectors.toMap((key) -> key, jsonObject::get));
            abstractDataWarehouseData.setExtension(extension);
        }
    }


    /**
     * SESSION_HEADER uppayload downpayload
     */
    private void append4Session(CsvRow csvRow, AbstractDataWarehouseData abstractDataWarehouseData) {
        try {
            putExtensionField(csvRow, HeadConst.CSV.UP_PAYLOAD, HeadConst.MONGO.UP_PAYLOAD_STRING, abstractDataWarehouseData);
            putExtensionField(csvRow, HeadConst.CSV.DOWN_PAYLOAD, "downPayload", abstractDataWarehouseData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * version ciphersuites handshake cerchain clientciphersuite servername random malformedpayload
     */
    private void append4Ssl(CsvRow csvRow, List<String> extensionKeys, AbstractDataWarehouseData abstractDataWarehouseData) {
        try {
            putExtensionKeys(csvRow, extensionKeys, abstractDataWarehouseData);
            putExtensionField(csvRow, HeadConst.CSV.VERSION, HeadConst.MONGO.VERSION_STRING, abstractDataWarehouseData);
            putExtensionField(csvRow, HeadConst.CSV.CIPHER_SUITES, HeadConst.MONGO.CIPHER_SUITES_STRING, abstractDataWarehouseData);
            putExtensionField(csvRow, HeadConst.CSV.HAND_SHAKE, HeadConst.MONGO.HAND_SHAKE_STRING, abstractDataWarehouseData);
            putExtensionField(csvRow, HeadConst.CSV.CERCHAIN, HeadConst.MONGO.CERCHAIN_STRING, abstractDataWarehouseData);
            putExtensionField(csvRow, HeadConst.CSV.CLIENT_CERCHAIN, HeadConst.MONGO.CLIENT_CERCHAIN_STRING, abstractDataWarehouseData);
            putExtensionField(csvRow, HeadConst.CSV.IS_DOUBLE, HeadConst.MONGO.IS_DOUBLE_STRING, abstractDataWarehouseData);
            putExtensionField(csvRow, HeadConst.CSV.CLIENT_CIPHER_SUITE, HeadConst.MONGO.CLIENT_CIPHER_SUITE_STRING, abstractDataWarehouseData);
            putExtensionField(csvRow, HeadConst.CSV.SERVER_NAME, HeadConst.MONGO.SERVER_NAME_STRING, abstractDataWarehouseData);
            putExtensionField(csvRow, HeadConst.CSV.RANDOM, HeadConst.MONGO.RANDOM_STRING, abstractDataWarehouseData);
            putExtensionField(csvRow, HeadConst.CSV.MALFORMED_UP_PAYLOAD, HeadConst.MONGO.UP_PAYLOAD_STRING, abstractDataWarehouseData);
            putExtensionField(csvRow, HeadConst.CSV.MALFORMED_DOWN_PAYLOAD, HeadConst.MONGO.DOWN_PAYLOAD_STRING, abstractDataWarehouseData);
            abstractDataWarehouseData.setKeyWord(csvRow.get(HeadConst.CSV.SERVER_NAME));
            String cerChain = csvRow.get(HeadConst.CSV.CERCHAIN);
            if (null != cerChain) {
                String sha1 = cerChain.split(";")[0].split("_")[0];
                Map<String, Object> cer = this.cerSelector.selector(sha1);
                if (null == abstractDataWarehouseData.getKeyWord() && null != cer && cer.containsKey(HeadConst.MONGO.SUBJECT_CN_STRING)) {
                    abstractDataWarehouseData.setKeyWord(cer.get(HeadConst.MONGO.SUBJECT_CN_STRING).toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同ssl
     */
    private void append4OpenVpn(CsvRow csvRow, List<String> extensionKeys, AbstractDataWarehouseData abstractDataWarehouseData) {
        append4Ssl(csvRow, extensionKeys, abstractDataWarehouseData);
    }

    /**
     * domain ips ext malformedpayload
     */
    private void append4Dns(CsvRow csvRow, List<String> extensionKeys, AbstractDataWarehouseData abstractDataWarehouseData) {
        putExtensionKeys(csvRow, extensionKeys, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.DOMAIN, HeadConst.MONGO.DOMAIN_STRING, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.IPS, HeadConst.MONGO.IPS, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.MALFORMED_UP_PAYLOAD, HeadConst.MONGO.UP_PAYLOAD_STRING, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.MALFORMED_DOWN_PAYLOAD, HeadConst.MONGO.DOWN_PAYLOAD_STRING, abstractDataWarehouseData);
        abstractDataWarehouseData.setKeyWord(csvRow.get(HeadConst.CSV.DOMAIN));
    }

    /**
     * ext host malformedpayload
     */
    private void append4Http(CsvRow csvRow, List<String> extensionKeys, AbstractDataWarehouseData abstractDataWarehouseData) {
        putExtensionKeys(csvRow, extensionKeys, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.URL_ROOT, HeadConst.MONGO.URL_ROOT_STRING, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.USER_AGENT, HeadConst.MONGO.USER_AGENT_STRING, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.MALFORMED_DOWN_PAYLOAD, HeadConst.MONGO.DOWN_PAYLOAD_STRING, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.HOST, HeadConst.MONGO.HOST_STRING, abstractDataWarehouseData);
        abstractDataWarehouseData.setKeyWord(csvRow.get(HeadConst.CSV.HOST));
    }

    /**
     * ext malformedpayload
     */
    private void append4Email(CsvRow csvRow, List<String> extensionKeys, AbstractDataWarehouseData abstractDataWarehouseData) {
        putExtensionKeys(csvRow, extensionKeys, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.MALFORMED_UP_PAYLOAD, HeadConst.MONGO.UP_PAYLOAD_STRING, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.MALFORMED_DOWN_PAYLOAD, HeadConst.MONGO.DOWN_PAYLOAD_STRING, abstractDataWarehouseData);
    }

    /**
     * ext malformedpayload
     */
    private void append4Ssh(CsvRow csvRow, List<String> extensionKeys, AbstractDataWarehouseData abstractDataWarehouseData) {
        putExtensionKeys(csvRow, extensionKeys, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.MALFORMED_UP_PAYLOAD, HeadConst.MONGO.UP_PAYLOAD_STRING, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.MALFORMED_DOWN_PAYLOAD, HeadConst.MONGO.DOWN_PAYLOAD_STRING, abstractDataWarehouseData);
    }

    /**
     * ext malformedpayload
     */
    private void append4FtpAndTelenet(CsvRow csvRow, List<String> extensionKeys, AbstractDataWarehouseData abstractDataWarehouseData) {
        putExtensionKeys(csvRow, extensionKeys, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.MALFORMED_UP_PAYLOAD, HeadConst.MONGO.UP_PAYLOAD_STRING, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.MALFORMED_DOWN_PAYLOAD, HeadConst.MONGO.DOWN_PAYLOAD_STRING, abstractDataWarehouseData);
    }

    /**
     * spi
     */
    private void append4EspAndAh(CsvRow csvRow, List<String> extensionKeys, AbstractDataWarehouseData abstractDataWarehouseData) {
        putExtensionKeys(csvRow, extensionKeys, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.SPI, HeadConst.MONGO.SPI_STRING, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.MALFORMED_UP_PAYLOAD, HeadConst.MONGO.UP_PAYLOAD_STRING, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.MALFORMED_DOWN_PAYLOAD, HeadConst.MONGO.DOWN_PAYLOAD_STRING, abstractDataWarehouseData);
    }

    /**
     * ext malformedpayload
     */
    private void append4Isakmp(CsvRow csvRow, List<String> extensionKeys, AbstractDataWarehouseData abstractDataWarehouseData) {
        putExtensionKeys(csvRow, extensionKeys, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.MALFORMED_UP_PAYLOAD, HeadConst.MONGO.UP_PAYLOAD_STRING, abstractDataWarehouseData);
        putExtensionField(csvRow, HeadConst.CSV.MALFORMED_DOWN_PAYLOAD, HeadConst.MONGO.DOWN_PAYLOAD_STRING, abstractDataWarehouseData);
    }

//    /**
//     * @param sessionUtils 就近udp会话上下行合并
//     * @return 0:不可合并 1：已合并
//     */
//    public final int append4UnknownUdp(SessionUtils sessionUtils) {
//        if ((this.extension.containsKey("uppayload") && sessionUtils.extension.containsKey("uppayload")) ||
//                (this.extension.containsKey("downpayload") && sessionUtils.extension.containsKey("downpayload"))
//        ) {
//            return 0;
//        }
//        this.upPkt += sessionUtils.upPkt;
//        this.upByte += sessionUtils.upByte;
//        this.downPkt += sessionUtils.downPkt;
//        this.downByte += sessionUtils.downByte;
//        this.extension.putAll(sessionUtils.extension);
//        return 1;
//    }

}
