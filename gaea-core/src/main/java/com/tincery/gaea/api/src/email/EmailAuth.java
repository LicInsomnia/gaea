package com.tincery.gaea.api.src.email;

import lombok.Getter;
import lombok.Setter;

/**
 * 邮件认证信息
 */
@Getter
@Setter
public class EmailAuth {
    /**
     * 1.优先级：eml中提取的Mail-X-Username
     *
     * 2.bcp中提取
     */
    private String userName;
    /**
     * 1.优先级：eml中提取的Mail-X-Password
     *
     * 2.bcp中提取
     */
    private String password;
}
