package com.tincery.gaea.api.base;

import com.tincery.starter.base.model.SimpleBaseDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * 应用实例化对象信息，apprule,httprule,applicationprotocolrule,payloadrule,dpidetector...
 *
 * @author gongxuanzhang
 */
@Setter
@Getter
@ToString
public class ApplicationInformationBO extends SimpleBaseDO {

    @Id
    private String id;

    private String key;

    private String title;

    private List<String> type;

    private List<String> specialTag;

    private Integer enc;

    private String proName;

    private Boolean ignore;

    /***这是一个属性的参照物 最终会归到enc这个属性上面  注解内容详情看spring-data-mongodb官网*/
    @Field("isenc")
    @AccessType(AccessType.Type.PROPERTY)
    private Boolean encConsult;

    /****
     * 这个方法是给spring data 提供的
     * @author gxz
     **/
    public ApplicationInformationBO setEncConsult(Boolean encConsult) {
        this.encConsult = encConsult;
        if (encConsult == null) {
            this.enc = -1;
            this.encConsult = false;
        } else {
            this.enc = encConsult ? 1 : 0;
        }

        return this;
    }


}
