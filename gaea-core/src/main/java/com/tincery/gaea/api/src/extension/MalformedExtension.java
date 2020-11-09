package com.tincery.gaea.api.src.extension;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MalformedExtension implements Serializable {

    private String malformedUpPayload;
    private String malformedDownPayload;

}
