package com.tincery.gaea.datawarehouse.reorganization.execute;

import com.tincery.gaea.core.dw.SessionFactory;
import com.tincery.starter.base.InitializationRequired;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Setter
@Getter
@Component
public class ReorganizationFactory implements InitializationRequired {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void init() {

    }

}
