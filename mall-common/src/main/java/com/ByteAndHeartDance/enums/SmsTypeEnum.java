package com.ByteAndHeartDance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信类型枚举
 */
@AllArgsConstructor
@Getter
public enum SmsTypeEnum {

    /**
     * 注册验证码
     */
    REGISTER(1, "注册验证码"),

    /**
     * 登录验证码
     */
    LOGIN(2, "登录验证码"),

    /**
     * 会员过期提醒
     */
    MEMBER_EXPIRE(3, "会员过期提醒");

    private Integer value;

    private String desc;
}
