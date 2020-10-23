package com.tincery.gaea.api.src;


import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

/**
 * @author gongxuanzhang
 */
@Setter
@Getter
public class SessionData extends AbstractSrcData {

    /**
     * 上下行载荷内容
     */
    private String upPayLoad;
    private String downPayLoad;

    @Override
    public void adjust() {
        super.adjust();
    }

    @Override
    public String toCsv(char splitChar) {
        Object[] join = new Object[]{super.toCsv(splitChar), this.getDuration(), this.getSyn(), this.getFin(), this.upPayLoad, this.downPayLoad};
        return Joiner.on(splitChar).useForNull("").join(join);
    }


}
