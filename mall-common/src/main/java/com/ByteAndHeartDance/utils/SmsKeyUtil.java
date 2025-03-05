package com.ByteAndHeartDance.utils;
import com.ByteAndHeartDance.enums.SmsTypeEnum;

import com.ByteAndHeartDance.exception.BusinessException;

/**
 * 短信验证码 key工具类
 */
public abstract class SmsKeyUtil {
    private static final String SMS_REGISTER_CODE_PREFIX = "smsRegisterCode:";
    private static final String SMS_LOGIN_CODE_PREFIX = "smsLoginCode:";

    private SmsKeyUtil() {

    }

    /**
     * 获取短信验证码Redis中的key
     *
     * @param phone 手机号
     * @param type  短信类型
     * @return key
     */
    public static String getSmsCodePrefixKey(String phone, SmsTypeEnum type) {
        return String.format("%s%s", getPrefix(type), phone);
    }

    private static String getPrefix(SmsTypeEnum type) {
        switch (type) {
            case LOGIN:
                return SMS_REGISTER_CODE_PREFIX;
            case REGISTER:
                return SMS_LOGIN_CODE_PREFIX;
            default:
                throw new BusinessException("短信类型错误");
        }

    }
}
