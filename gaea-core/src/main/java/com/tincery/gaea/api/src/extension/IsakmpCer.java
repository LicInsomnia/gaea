package com.tincery.gaea.api.src.extension;

import com.alibaba.fastjson.JSONObject;
import com.tincery.gaea.core.base.component.support.CerSelector;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Insomnia
 */
@Getter
@Setter
public class IsakmpCer implements Serializable {

    private String sha1;
    private String certEncoding;
    private JSONObject cer;

    public IsakmpCer() {

    }

    public IsakmpCer(String sha1, String certEncoding) {
        this.sha1 = sha1;
        this.certEncoding = certEncoding;
    }

    public void adjust(CerSelector cerSelector) {
        this.cer = cerSelector.selector(this.sha1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IsakmpCer)) {
            return false;
        }
        IsakmpCer isakmpCer = (IsakmpCer) o;
        return sha1.equals(isakmpCer.sha1) &&
                certEncoding.equals(isakmpCer.certEncoding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sha1, certEncoding);
    }

}
