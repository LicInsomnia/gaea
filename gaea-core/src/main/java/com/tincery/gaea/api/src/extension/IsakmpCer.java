package com.tincery.gaea.api.src.extension;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class IsakmpCer {

    String sha1;
    String certEncoding;

    public boolean isNotEmpty() {
        return null != this.sha1 && null != this.certEncoding;
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
