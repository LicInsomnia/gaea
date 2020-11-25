package com.tincery.gaea.api.src.extension;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.component.support.CerSelector;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author Insomnia
 */
@Getter
@Setter
public class SslCer {

    private final String sha1;
    private JSONObject cer;

    public SslCer(String sha1) {
        this.sha1 = sha1;
    }

    public void adjust(CerSelector cerSelector) {
        this.cer = cerSelector.selector(this.sha1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SslCer)) {
            return false;
        }
        SslCer sslCer = (SslCer) o;
        return sha1.equals(sslCer.sha1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sha1);
    }

}
