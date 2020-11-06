package com.tincery.gaea.core.dw;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.dw.AbstractDataWarehouseData;
import com.tincery.gaea.api.src.extension.*;
import com.tincery.gaea.core.base.component.support.ApplicationProtocol;
import com.tincery.gaea.core.base.component.support.CerSelector;
import com.tincery.gaea.core.base.component.support.DnsRequest;
import com.tincery.gaea.core.base.component.support.IpSelector;
import com.tincery.gaea.core.base.mgt.HeadConst;
import com.tincery.gaea.core.base.plugin.csv.CsvRow;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class SessionFactory {

    @Autowired
    private IpSelector ipSelector;
    @Autowired
    private CerSelector cerSelector;
    @Autowired
    private ApplicationProtocol applicationProtocol;
    @Autowired
    private DnsRequest dnsRequest;

    /**
     * 从csv数据中抽象
     *
     * @param category csv类型
     * @param csvRow   csv某一行{@link CsvRow}
     * @return 抽象是否成功
     */
    public AbstractDataWarehouseData create(String category, CsvRow csvRow) {
        AbstractDataWarehouseData data = appendBaseAndAttach(category, csvRow);
        adjust(data);
        if (data.getMalFormed()) {
            append4Malformed(csvRow, data);
        } else {
            switch (category) {
                case "session":
                    this.append4Session(csvRow, data);
                    break;
                case "ssl":
                    this.append4Ssl(csvRow, data);
                    break;
                case "openvpn":
                    this.append4OpenVpn(csvRow, data);
                    break;
                case "dns":
                    this.append4Dns(csvRow, data);
                    break;
                case "http":
                    this.append4Http(csvRow, data);
                    break;
                case "email":
                    this.append4Email(csvRow, data);
                    break;
                case "isakmp":
                    this.append4Isakmp(csvRow, data);
                    break;
                case "ssh":
                    this.append4Ssh(csvRow, data);
                    break;
                case "ftp_telnet":
                    this.append4FtpAndTelnet(csvRow, data);
                    break;
                case "esp_ah":
                    this.append4EspAndAh(csvRow, data);
                    break;
                default:
                    return null;
            }
        }
        return data;
    }

    private AbstractDataWarehouseData appendBaseAndAttach(String category, CsvRow csvRow) {
        AbstractDataWarehouseData data = new AbstractDataWarehouseData();
        data.setGroupName(csvRow.getEmptyNull(HeadConst.FIELD.GROUP_NAME))
                .setTargetName(csvRow.getEmptyNull(HeadConst.FIELD.TARGET_NAME))
                .setUserId(csvRow.getEmptyNull(HeadConst.FIELD.USER_ID))
                .setServerId(csvRow.getEmptyNull(HeadConst.FIELD.SERVER_ID))
                .setSource(csvRow.getEmptyNull(HeadConst.FIELD.SOURCE))
                .setCapTime(csvRow.getLong(HeadConst.FIELD.CAPTIME))
                .setClientMac(csvRow.getEmptyNull(HeadConst.FIELD.CLIENT_MAC))
                .setServerMac(csvRow.getEmptyNull(HeadConst.FIELD.SERVER_MAC))
                .setProtocol(csvRow.getInteger(HeadConst.FIELD.PROTOCOL))
                .setProName(csvRow.getEmptyNull(HeadConst.FIELD.PRONAME))
                .setClientIp(csvRow.getEmptyNull(HeadConst.FIELD.CLIENT_IP))
                .setServerIp(csvRow.getEmptyNull(HeadConst.FIELD.SERVER_IP))
                .setClientPort(csvRow.getInteger(HeadConst.FIELD.CLIENT_PORT))
                .setServerPort(csvRow.getInteger(HeadConst.FIELD.SERVER_PORT))
                .setClientIpOuter(csvRow.getEmptyNull(HeadConst.FIELD.CLIENT_IP_OUTER))
                .setServerIpOuter(csvRow.getEmptyNull(HeadConst.FIELD.SERVER_IP_OUTER))
                .setClientPortOuter(csvRow.getInteger(HeadConst.FIELD.CLIENT_PORT_OUTER))
                .setServerPortOuter(csvRow.getInteger(HeadConst.FIELD.SERVER_PORT_OUTER))
                .setProtocolOuter(csvRow.getInteger(HeadConst.FIELD.PROTOCOL_OUTER))
                .setUpPkt(csvRow.getLong(HeadConst.FIELD.UP_PKT))
                .setUpByte(csvRow.getLong(HeadConst.FIELD.UP_BYTE))
                .setDownPkt(csvRow.getLong(HeadConst.FIELD.DOWN_PKT))
                .setDownByte(csvRow.getLong(HeadConst.FIELD.DOWN_BYTE))
                .setDataType(csvRow.getInteger(HeadConst.FIELD.DATA_TYPE))
                .setImsi(csvRow.getEmptyNull(HeadConst.FIELD.IMSI))
                .setImei(csvRow.getEmptyNull(HeadConst.FIELD.IMEI))
                .setMsisdn(csvRow.getEmptyNull(HeadConst.FIELD.MSISDN))
                .setForeign(csvRow.getBoolean(HeadConst.FIELD.FOREIGN))
                .setDuration(csvRow.getLong(HeadConst.FIELD.DURATION))
                .setSyn(csvRow.getBoolean(HeadConst.FIELD.SYN_FLAG))
                .setFin(csvRow.getBoolean(HeadConst.FIELD.FIN_FLAG));
        data.setClientLocation(this.ipSelector.getCommonInformation(data.getClientIp()))
                .setServerLocation(this.ipSelector.getCommonInformation(data.getServerIp()))
                .setDataSource(category);
        String caseTags = csvRow.get(HeadConst.FIELD.CASE_TAGS);
        if (null != caseTags) {
            data.setCaseTags(new HashSet<>(Arrays.asList(caseTags.split(";"))));
        }
        data.setAssetFlag((Integer) csvRow.getExtensionValue(HeadConst.FIELD.ASSET_FLAG));
        String id = Joiner.on("_").useForNull("").join(new Object[]{
                data.getTargetName(),
                data.getProtocol(),
                data.getClientIp(),
                data.getServerIp(),
                data.getClientPort(),
                data.getServerPort(),
                data.getCapTime()
        });
        return data.setId(id);
    }

    private void adjust(AbstractDataWarehouseData data) {
        // 设置dnsRequest
        data.setProtocolKnown(!HeadConst.PRONAME.OTHER.equals(data.getProName()));
        data.setMalFormed(Objects.equals(-1, data.getDataType()));
        if (!HeadConst.PRONAME.DNS.equals(data.getProName())) {
            String key = data.getUserId() + "_" + data.getServerIp();
            data.setDnsRequestBO(this.dnsRequest.getDnsRequest(key, data.getCapTime()));
            if (null == data.getKeyWord() && null != data.getDnsRequestBO()) {
                data.setKeyWord(data.getDnsRequestBO().getDomain());
            }
        }
        // 设置malformed
        if (!data.getMalFormed()) {
            data.setEnc(this.applicationProtocol.isEnc(data.getProName()));
        }
        // 设置tag
        if (data.getProtocol() == 6 && ((data.getUpByte() + data.getDownByte()) == 0)) {
            data.setTag("SYN");
            return;
        }
        /* 是否为netproc已解析协议 */
        if (null == data.getMalFormed()) {
            /* netproc未解析协议 */
            if (HeadConst.PRONAME.OTHER.equals(data.getProName())) {
                /* 特殊端口 */
                data.setTag("特殊端口");
            } else {
                /* 已知端口 */
                data.setTag(data.getProName().replace("(payload)", ""));
            }
        } else {
            /* netproc已解析协议 */
            if (data.getMalFormed()) {
                /* 异常协议 */
                if (HeadConst.PRONAME.OTHER.equals(data.getProName())) {
                    /* 未知异常协议 */
                    data.setTag("异常协议");
                } else {
                    /* 端口仿冒 */
                    data.setTag("端口仿冒");
                }
            } else {
                /* 正常协议 */
                data.setTag(data.getProName().replace("(payload)", ""));
            }
        }
    }

    private void append4Malformed(CsvRow csvRow, AbstractDataWarehouseData data) {
        MalformedExtension malformedExtension = new MalformedExtension();
        malformedExtension.setMalformedUpPayload(csvRow.getEmptyNull(HeadConst.FIELD.MALFORMED_UP_PAYLOAD));
        malformedExtension.setMalformedDownPayload(csvRow.getEmptyNull(HeadConst.FIELD.MALFORMED_DOWN_PAYLOAD));
        data.setMalformedExtension(malformedExtension);
    }

    private void append4Session(CsvRow csvRow, AbstractDataWarehouseData data) {
        SessionExtension sessionExtension = JSONObject.toJavaObject(csvRow.getJsonObject(HeadConst.FIELD.EXTENSION), SessionExtension.class);
        data.setSessionExtension(sessionExtension);
        data.setExtensionFlag(data.getDataSource());
    }

    private void append4Ssl(CsvRow csvRow, AbstractDataWarehouseData data) {
        SslExtension sslExtension = JSONObject.toJavaObject(csvRow.getJsonObject(HeadConst.FIELD.EXTENSION), SslExtension.class);
        data.setSslExtension(sslExtension);
        data.setExtensionFlag(data.getDataSource());
        data.setKeyWord(sslExtension.getServerName());
        String sha1 = sslExtension.getSha1();
        if (null != sha1) {
            sha1 = sha1.split("_")[0];
            Map<String, Object> cer = this.cerSelector.selector(sha1);
            if (null == data.getKeyWord() && null != cer) {
                data.setKeyWord(cer.get("subject_cn").toString());
            }
        }
    }

    private void append4OpenVpn(CsvRow csvRow, AbstractDataWarehouseData data) {
        OpenVpnExtension openVpnExtension = JSONObject.toJavaObject(csvRow.getJsonObject(HeadConst.FIELD.EXTENSION), OpenVpnExtension.class);
        data.setOpenVpnExtension(openVpnExtension);
        data.setExtensionFlag(data.getDataSource());
        data.setKeyWord(openVpnExtension.getServerName());
        String sha1 = openVpnExtension.getSha1();
        if (null != sha1) {
            sha1 = sha1.split("_")[0];
            Map<String, Object> cer = this.cerSelector.selector(sha1);
            if (null == data.getKeyWord() && null != cer) {
                data.setKeyWord(cer.get("subject_cn").toString());
            }
        }
    }

    private void append4Dns(CsvRow csvRow, AbstractDataWarehouseData data) {
        DnsExtension dnsExtension = JSONObject.toJavaObject(csvRow.getJsonObject(HeadConst.FIELD.EXTENSION), DnsExtension.class);
        data.setDnsExtension(dnsExtension);
        data.setExtensionFlag(data.getDataSource());
        data.setKeyWord(dnsExtension.getDomain());
    }

    private void append4Http(CsvRow csvRow, AbstractDataWarehouseData data) {
        HttpExtension httpExtension = JSONObject.toJavaObject(csvRow.getJsonObject(HeadConst.FIELD.EXTENSION), HttpExtension.class);
        data.setHttpExtension(httpExtension);
        data.setExtensionFlag(data.getDataSource());
        data.setKeyWord(httpExtension.getHost().get(0));
    }

    private void append4Email(CsvRow csvRow, AbstractDataWarehouseData data) {
        data.setExtensionFlag(data.getDataSource());
    }

    private void append4Ssh(CsvRow csvRow, AbstractDataWarehouseData data) {
        SshExtension sshExtension = JSONObject.toJavaObject(csvRow.getJsonObject(HeadConst.FIELD.EXTENSION), SshExtension.class);
        data.setSshExtension(sshExtension);
        data.setExtensionFlag(data.getDataSource());
    }

    private void append4FtpAndTelnet(CsvRow csvRow, AbstractDataWarehouseData data) {
        FtpAndTelnetExtension ftpAndTelnetExtension = JSONObject.toJavaObject(csvRow.getJsonObject(HeadConst.FIELD.EXTENSION), FtpAndTelnetExtension.class);
        data.setFtpAndTelnetExtension(ftpAndTelnetExtension);
        data.setExtensionFlag(data.getDataSource());
    }

    private void append4EspAndAh(CsvRow csvRow, AbstractDataWarehouseData data) {
        EspAndAhExtension espAndAhExtension = JSONObject.toJavaObject(csvRow.getJsonObject(HeadConst.FIELD.EXTENSION), EspAndAhExtension.class);
        data.setEspAndAhExtension(espAndAhExtension);
        data.setExtensionFlag(data.getDataSource());
    }

    private void append4Isakmp(CsvRow csvRow, AbstractDataWarehouseData data) {
        IsakmpExtension isakmpExtension = JSONObject.toJavaObject(csvRow.getJsonObject(HeadConst.FIELD.EXTENSION), IsakmpExtension.class);
        data.setIsakmpExtension(isakmpExtension);
        data.setExtensionFlag(data.getDataSource());
    }

}
