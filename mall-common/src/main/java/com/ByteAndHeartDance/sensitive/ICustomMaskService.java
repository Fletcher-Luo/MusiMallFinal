package com.ByteAndHeartDance.sensitive;

/**
 * 处理脱敏数据的接口
 */
public interface ICustomMaskService {
    /**
     * 脱敏方法
     *
     * @param data 数据
     * @return 脱敏后的数据
     */
    String maskData(String data);
}
