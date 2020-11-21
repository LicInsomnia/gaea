package com.tincery.gaea.api.src.email;


import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 邮件关系列表
 */
@Getter
@Setter
public class EmailTransceiverRelationship {

    /*发件人 eml中提取 	对应原from字段*/
    private List<String> sender;
    /*收件人 eml中提取 	对应原to字段*/
    private List<String> recipient;
    /*抄送 eml中提取 对应原cc字段*/
    private List<String> cc;
    /*密送 eml中提取	对应原bcc字段*/
    private List<String> secretDelivery;
    /*邮件联系人  将收件人、发件人、抄送、密送拼接起来 接起来	对应原mailaddr字段*/
    private List<String> mailAddr;
    /*邮箱域名 对应原domain字段*/
    private String domain;


    public void fixRecipient(String person){
        if (StringUtils.isEmpty(person)){
            return;
        }
        if (CollectionUtils.isEmpty(this.recipient)){
            this.recipient = new ArrayList<>();
        }
        this.recipient.add(person);
    }

    public void fixSender(String person){
        if (StringUtils.isEmpty(person)){
            return;
        }
        if (CollectionUtils.isEmpty(this.sender)){
            this.sender = new ArrayList<>();
        }
        this.sender.add(person);
    }

    public void fixCc(String person) {
        if (StringUtils.isEmpty(person)){
            return;
        }
        if (CollectionUtils.isEmpty(this.cc)){
            this.cc = new ArrayList<>();
        }
        this.cc.add(person);
    }

    public void fixSecretDelivery(String person) {
        if (StringUtils.isEmpty(person)){
            return;
        }
        if (CollectionUtils.isEmpty(this.secretDelivery)){
            this.secretDelivery = new ArrayList<>();
        }
        this.secretDelivery.add(person);
    }

    public void fixMailAddrAndDomain(){
        Set<String> domainTemp = new HashSet<>();
        if (CollectionUtils.isEmpty(this.mailAddr)){
            this.mailAddr = new ArrayList<>();
        }
        if (null != this.sender) {
            for (String str : this.sender) {
                domainTemp.add(filterDomain(str));
            }
            this.mailAddr.addAll(this.sender);
        }
        if (null != this.recipient) {
            for (String str : this.recipient) {
                domainTemp.add(filterDomain(str));
            }
            this.mailAddr.addAll(this.recipient);
        }
        if (null != this.cc) {
            for (String str : this.cc) {
                domainTemp.add(filterDomain(str));
            }
            this.mailAddr.addAll(this.cc);
        }
        if (null != this.secretDelivery) {
            for (String str : this.secretDelivery) {
                domainTemp.add(filterDomain(str));
            }
            this.mailAddr.addAll(this.secretDelivery);
        }

        this.domain = String.join(";", domainTemp);
    }

    private String filterDomain(String domain) {
        return domain
                .substring(domain.lastIndexOf('@') + 1)
                .replace(">", "")
                .replace(";", "");
    }
}
