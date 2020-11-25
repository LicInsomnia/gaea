package com.tincery.gaea.api.src.extension;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author Insomnia
 */
@Getter
@Setter
public class Life {

    private Long lifeDuration;
    private String lifeType;

    public Life(Long lifeDuration, String lifeType) {
        this.lifeDuration = lifeDuration;
        this.lifeType = lifeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Life)) return false;
        Life life = (Life) o;
        return lifeDuration.equals(life.lifeDuration) &&
                lifeType.equals(life.lifeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lifeDuration, lifeType);
    }
}
