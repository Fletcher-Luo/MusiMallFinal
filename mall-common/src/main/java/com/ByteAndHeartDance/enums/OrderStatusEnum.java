package com.ByteAndHeartDance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@AllArgsConstructor
@Getter
public enum OrderStatusEnum {

    /**
     * 已下单/待支付
     */
    CREATE(1, "已下单"),

    /**
     * 已支付
     */
    PAY(2, "已支付"),

    /**
     * 已发货
     */
    SHIPPED(3, "已发货"),

    /**
     * 已完成
     */
    FINISH(4, "已完成"),

    /**
     * 已取消
     */
    CANCEL(5, "已取消"),

    /**
     * 已退货
     */
    REJECT(6, "已退货"),

    /**
     * 已评价
     */
    COMMENT(7, "已评价");

    private Integer value;

    private String desc;
}
