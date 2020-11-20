package com.tincery.gaea.api.src.extension;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FtpAndTelnetExtension implements Serializable {

    private String userName;
    private String password;
    private String conflg;
    private String sucflg;

    private String content;

    public String toCsv(char splitChar) {
        Object[] join = new Object[]{this.userName, this.password, this.conflg, this.sucflg, this.content};
        return Joiner.on(splitChar).useForNull("").join(join);
    }
}
