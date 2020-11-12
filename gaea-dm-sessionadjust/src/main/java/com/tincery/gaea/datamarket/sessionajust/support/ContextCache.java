package com.tincery.gaea.datamarket.sessionajust.support;

import com.tincery.gaea.api.base.ApplicationInformationBO;
import com.tincery.starter.base.InitializationRequired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Insomnia
 */
@Component
public class ContextCache implements InitializationRequired {

    Map<String, ApplicationInformationBO> map = new ConcurrentHashMap<>();

    @Override
    public void init() {

    }

}
