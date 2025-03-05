package com.ByteAndHeartDance.shoppingcart.entity;


import com.ByteAndHeartDance.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
@Tag(name = "购物车信息查询实体", description = "购物车信息查询实体")
public class ShoppingCartConditionEntity extends RequestConditionEntity {
    /** ID集合 */
    private List<Long> idList;

    /** ID */
    private Long id;

    /** 商品ID */
    private Long productId;

    /** 用户ID */
    private Long userId;

    /**
     *  数量
     */
    private Integer quantity;

    /**
     *  价格
     */
    private BigDecimal price;
    /**
     *  总金额
     */
    private BigDecimal totalAmount;

    /**
     *  创建人ID
     */
    private Long createUserId;
    /**
     *  创建人名称
     */
    private String createUserName;

    /**
     *  修改人ID
     */
    private Long updateUserId;
    /**
     *  修改人名称
     */
    private String updateUserName;

    /**
     *  是否删除 1：已删除 0：未删除
     */
    private Integer isDel;

}
