package com.tincery.gaea.api.src;


import com.google.common.base.Joiner;
import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.gaea.api.base.Location;
import com.tincery.gaea.core.base.component.ApplicationCheck;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class EmailData extends AbstractSrcData implements Cloneable{

    private List<String> rcpt;
    private Location clientLocation;
    private Location serverLocation;
    List<String> attachSuffixList;

    List<Communication> emailDataList;

    Communication emailData;


    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{};
//        Object[] join = new Object[]{super.toCsv(splitChar), this.durationTime, this.syn, this.fin, this.serverName, this.sha1
//                , formatList(this.cerChain), formatList(this.clientCerChain), this.isDouble, this.random, formatList(this.version)
//                , formatList(this.cipherSuites), this.clientCipherSuite, this.handshake, this.malformedUpPayload, this.malformedDownPayload};
        return Joiner.on(splitChar).useForNull("").join(join);
    }



    public List<EmailData> split() {
        List<EmailData> result  = new ArrayList<>(this.emailDataList.size());
        this.setEmailDataList(null);
        for (Communication emailDatum : this.emailDataList) {
                EmailData tempData =  this.clone();
                tempData.setEmailData(emailDatum);
                result.add(tempData);
        }
        return result;
    }


    @Override
    public void adjust() {
        if ("0.0.0.0".equals(this.clientIpOuter)) {
            this.setClientIpOuter(null);
        }
        if ("0.0.0.0".equals(this.serverIpOuter)) {
            this.setServerIpOuter(null);
        }
        if (0 == this.clientPortOuter) {
            this.setClientPortOuter(null);
        }
        if (0 == this.serverPortOuter) {
            this.setServerPortOuter(null);
        }
        if (0 == this.protocolOuter) {
            this.setProtocolOuter(null);
        }
    }

    @Override
    public EmailData clone() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            EmailData deepClone = (EmailData) ois.readObject();
            return deepClone;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Setter
    @Getter
    @ToString
    public static class Communication implements Serializable{
        private Set<String> domain = new HashSet<>();
        private Set<String> domainTag = new HashSet<>();
        private List<Document> language = new ArrayList<>();
        private List<String> mailAddress = new ArrayList<>();
        private String key;
        private String messageId;
        private String date;
        private Integer priority;
        private String received;
        private String subject;
        private String charset;
        private String content;
        private String contentTxt;
        private String from;
        private List<String> to;
        private List<String> cc;
        private List<String> bcc;
        private String fileName;
        private Integer mailLength;
        private String transmitter;
        private String charsetOut;
        private List<Document> attach;


        private String filterDomain(String domain) {
            return domain
                    .substring(domain.lastIndexOf('@') + 1)
                    .replace(">", "")
                    .replace(";", "");
        }

        /**
         * 添加联系人信息
         **/
        private void contact(ApplicationCheck applicationCheck) {
            if (null != this.from) {
                this.domain.add(filterDomain(this.from));
                this.mailAddress.add(this.from);
            }
            if (null != this.to) {
                for (String str : this.to) {
                    this.domain.add(filterDomain(str));
                }
                this.mailAddress.addAll(this.to);
            }
            if (null != this.cc) {
                for (String str : this.cc) {
                    this.domain.add(filterDomain(str));
                }
                this.mailAddress.addAll(this.cc);
            }
            if (null != this.bcc) {
                for (String str : this.bcc) {
                    this.domain.add(filterDomain(str));
                }
                this.mailAddress.addAll(this.bcc);
            }
            for (String domain : this.domain) {
                ApplicationInformationBO application = applicationCheck.getApplicationInformation(domain);
                if (null != application) {
                    this.domainTag.add(application.getTitle());
                }
            }

        }

    }


}
