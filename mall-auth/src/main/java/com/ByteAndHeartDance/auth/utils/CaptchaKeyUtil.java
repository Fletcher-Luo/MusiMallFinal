package com.ByteAndHeartDance.auth.utils;

/**
 *  * 验证码 key工具类
 */
public abstract class CaptchaKeyUtil {
    private static final String CAPTCHA_PREFIX = "captcha:";

    private CaptchaKeyUtil() {

    }

    /**
     * 获取验证码Redis的key
     *
     * @param uuid 验证码uuid
     * @return 验证码Redis的key
     */
    public static String getCaptchaKey(String uuid) {
        return String.format("%s%s", CAPTCHA_PREFIX, uuid);
    }
}
