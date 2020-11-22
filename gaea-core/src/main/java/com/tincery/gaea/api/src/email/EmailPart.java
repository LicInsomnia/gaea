package com.tincery.gaea.api.src.email;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Email要素信息
 */
@Getter
@Setter
public class EmailPart {

    /*邮件名*/
    private String fileName;
    /**
     * 是否满足布控或预警
     *
     * 3-满足预警与布控条件
     *
     * 2-满足预警条件
     *
     * 1-满足布控条件
     */
    private Integer matchFlag;

    /*当前时间*/
    private Long uploadDate;
    /*附件总数*/
    private Integer annexTotalNum;
    /*加密附件总数*/
    private Integer encryptAnnexTotalNum;
    /*未加密附件总数*/
    private Integer unEncryptAnnexTotalNum;
    /*未知附件总数*/
    private Integer unknownAnnexTotalNum;

    /*发送时间  eml提取*/
    private Long sendDate;
    /*主题 eml提取*/
    private String subject;
    /**
     * 正文
     * eml提取plain内容，如果没有plain，从html中提取
     */
    private String text;
    /*正文长度*/
    private Integer textLength;
    /*html文件地址 eml提取html内容*/
    private String textHtml;
    /*eml文件地址 带子路径的邮件名*/
    private String emailPath;
    /*eml Header中received信息*/
    private String received;
    /*eml Header中X-Mailer信息*/
    private String xMailer;
    /*数据来源IP信息*/
    private String xOriginatingIP;
    /*数据来源IP int64类型*/
    private Long xOriginatingIpConvert;
    /*数据来源IP地址信息*/
    private String realIpAddress;
    /*数据来源IP*/
    private String dataSourceIp;
    /*目标邮件服务器*/
    private String deliveredTo;
    /*邮箱密码*/
    private String mailXPassword;
    /*读取状态 0-未读*/
    private String workStatus;
    /*备注*/
    private String remark;
    /*内嵌资源地址*/
    private List<String> htmlAnnexDetail;
    /*邮件ID*/
    private String messageID;
    /*字符集*/
    private String charset;
    /*CharsetOut*/
    private String charsetOut;
    /*优先级*/
    private String priority;
    /*邮件大小*/
    private String mailLen;
    /*收发行为*/
    private String transmitter;


    /*附件相关*/
    private EmailAnnexDetail annexDetail;

    /*关联关系相关*/
    private EmailTransceiverRelationship transceiverRelationship;


}
