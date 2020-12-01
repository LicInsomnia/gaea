package com.tincery.gaea.api.src.extension;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BitCoinExtension implements Serializable {
    private String sigr;
    private String sigs;
    private String key;
}
