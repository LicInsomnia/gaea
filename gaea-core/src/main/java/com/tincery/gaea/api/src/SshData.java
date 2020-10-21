package com.tincery.gaea.api.src;


import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 */
@Setter
@Getter
public class SshData extends AbstractSrcData {

    /**
     * 上下行载荷内容
     */
    private String upPayLoad;
    private String downPayLoad;

    private List<String> messageList;

    @Override
    public void adjust() {
        super.adjust();
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{super.toCsv(splitChar), this.getDurationTime() / 1000, this.getSyn(), this.getFin(), this.upPayLoad, this.downPayLoad};
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
