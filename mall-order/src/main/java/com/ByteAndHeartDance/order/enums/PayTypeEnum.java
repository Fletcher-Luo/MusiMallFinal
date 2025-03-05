package com.ByteAndHeartDance.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付状态枚举
 */
@AllArgsConstructor
@Getter
public enum PayTypeEnum {

    /**
     * 支付宝
     */
    ALIPAY(1, "支付宝"),

    /**
     * 已支付
     */
    COIN(2, "金币");



    private Integer value;

    private String desc;
}
