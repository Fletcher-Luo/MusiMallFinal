package com.ByteAndHeartDance.order.entity.order.tradeItem;

import com.ByteAndHeartDance.entity.RequestConditionEntity;
// import com.ByteAndHeartDance.enums.OrderStatusEnum;
// import com.ByteAndHeartDance.order.enums.PayStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单明细查询条件实体
 *
 * @author jjq0425
 * @date 2025-02-05
 */
@Schema(description = "订单明细查询条件实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeItemConditionEntity extends RequestConditionEntity {
  /** ID */
  @Schema(description = "ID")
  private Long id;

  /** 订单ID */
  @Schema(description = "订单ID")
  private Long tradeId;

  /** 商品ID */
  @Schema(description = "商品ID")
  private Long productId;

  /** 商品名称 */
  @Schema(description = "商品名称")
  private String productName;

  /** 商品规格 */
  @Schema(description = "商品规格")
  private String model;

  /** 单价 */
  @Schema(description = "商品单价")
  private BigDecimal price;

  /** 数量 */
  @Schema(description = "购买数量")
  private Integer quantity;

  /** 金额 */
  @Schema(description = "购买商品总金额")
  private BigDecimal amount;

    /** 商品图片 */
    @Schema(description = "商品图片")
    private String picture;

  /** 创建人ID */
  @Schema(description = "创建人ID")
  private Long createUserId;

  /** 创建人名称 */
  @Schema(description = "创建人名称")
  private String createUserName;

  /** 创建日期 */
  @Schema(description = "创建日期")
  private Date createTime;

  /** 修改人ID */
  @Schema(description = "修改人ID")
  private Long updateUserId;

  /** 修改人名称 */
  @Schema(description = "修改人名称")
  private String updateUserName;

  /** 修改时间 */
  @Schema(description = "修改时间")
  private Date updateTime;

  /** 是否删除 1：已删除 0：未删除 */
  @Schema(description = "是否删除 1：已删除 0：未删除")
  private Integer isDel;
}
