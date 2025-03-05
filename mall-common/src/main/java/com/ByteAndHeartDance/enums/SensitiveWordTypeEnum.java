package com.ByteAndHeartDance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 敏感词类型
 */
@Getter
@AllArgsConstructor
public enum SensitiveWordTypeEnum {

    POLITICS(1, "政治"),

    LAWLESS(2, "违法"),

    SEX(3, "色情"),

    AD(4, "广告"),

    WEBSITE(5, "网址");

    /**
     * 枚举值
     */
    private Integer value;


    /**
     * 枚举描述
     */
    private String desc;
}
