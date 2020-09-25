package com.tincery.gaea.core.base.exception;

import lombok.NoArgsConstructor;

/**
 * @author gxz gongxuanzhang@foxmail.com
 * 初始化异常
 **/
@NoArgsConstructor
public class InitException extends RuntimeException {

    public InitException(String message) {
        super(message);
    }
}
