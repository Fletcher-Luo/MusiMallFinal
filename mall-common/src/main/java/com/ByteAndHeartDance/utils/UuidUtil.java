package com.ByteAndHeartDance.utils;

import java.util.UUID;

/**
 * uuid工具
 */
public abstract class UuidUtil {

    private UuidUtil() {
    }

    /**
     * 获取uuid字符串
     *
     * @return uuid字符串
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
