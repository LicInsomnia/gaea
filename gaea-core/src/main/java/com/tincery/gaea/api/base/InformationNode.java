package com.tincery.gaea.api.base;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author Insomnia
 */
@Getter
@Setter
public class InformationNode {

    private String name;
    private Object value;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InformationNode that = (InformationNode) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return name + "_" + value;
    }

}
