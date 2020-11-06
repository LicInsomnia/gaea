package com.tincery.gaea.api.src;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.tincery.gaea.api.src.extension.IsakmpExtension;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IsakmpData extends AbstractSrcData {

    private IsakmpExtension isakmpExtension;

    @Override
    public void adjust() {
        super.adjust();
        /**
         * 协议版本	                            proVersion                  如果会话是malformed，为"非标准IPSEC"
         *                                                                  如果isakmp协商之后使用UDP 4500端口通信，则为“IPSEC VPN（NAT模式）”
         *                                                                  否则为"IPSEC VPN"
         * 密钥交换第一阶段模式	                firstMode                   如果是messagelist包含"Aggressive"，则为"野蛮模式"；
         *                                                                  如果messagelist不包含"Identity Protection (Main Mode) "，并且不包含”Aggressive”，则为"-"
         *                                                                  否则为"主模式"
         * 密钥交换第二阶段模式	                secondMode	                如果是messagelist包含" Quick Mode"，则为"快速模式"，否则为”-“
         * 响应方密钥交换第一阶段交换数据完整性		responderComplete           如果responder_information的Private Use Data、Nonce Data、Identification Data、Signature Data均存在，则显示"完整"，会话是malformed，则显示"-"，否则显示"不完整"
         * 发起方密钥交换第一阶段交换数据完整性		initiatorComplete           如果initiator_information的Private Use Data、Nonce Data、Identification Data、Signature Data均存在，则显示"完整"，如果会话是malformed，则显示"-"，否则显示"不完整"
         * 响应方证书编码                         responderCertEncoding
         * 响应方证书SHA1		                    responderSha1               responder_information.cert[i]. Cert Encoding
         * responder_information.cert[i].sha1
         * 发起方证书编码
         * 发起方证书SHA1		initiator_information.cert[i]. Cert Encoding
         * initiator_information.cert[i].sha1
         * 其中证书编码与txt值对应如下表
         * 第二阶段密钥交换完整性			如果没有发起方和响应方的“Quick Mode“数据，显示”-“
         * 	如果发起方和响应方的“(Exchange Type = Quick Mode) + Message ID + 载荷长度”去重后大于一个，表示完整，显示“完整”
         * 	否则，显示“不完整“
         * 注意：发起方和响应方数据放在一起比
         * 是否存在加密的鉴别数据		如果是"Identity Protection (Main Mode) "，并且 “payload: Hash”存在，则为true，否则为false
         */
    }

    @Override
    public String toCsv(char splitChar) {
        String extension = null;
        if (null != this.isakmpExtension) {
            extension = JSONObject.toJSONString(this.isakmpExtension);
        }
        Object[] join = new Object[]{super.toCsv(splitChar),
                this.malformedUpPayload, this.malformedDownPayload,
                extension
        };
        return Joiner.on(splitChar).useForNull("").join(join);
    }

}
