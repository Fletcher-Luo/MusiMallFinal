package com.ByteAndHeartDance.auth.dto.auth;

import lombok.Data;

/**
 * 前端菜单所需meta实体
 */
@Data
public class MetaDTO {

    /**
     * icon
     */
    private String icon;

    /**
     * 是否不缓存
     */
    private Boolean noCache;

    /**
     * 菜单标题
     */
    private String title;
}
