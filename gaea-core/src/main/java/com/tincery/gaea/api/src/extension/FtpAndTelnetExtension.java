package com.tincery.gaea.api.src.extension;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FtpAndTelnetExtension {

    private String userName;
    private String password;
    private String conflg;
    private String sucflg;

    private String content;

    public String toCsv(char splitChar) {
        Object[] join = new Object[]{this.userName,this.password,this.conflg,this.sucflg,this.content};
        return Joiner.on(splitChar).useForNull("").join(join);
    }
}
