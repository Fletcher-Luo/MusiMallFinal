package com.ByteAndHeartDance.order.entity.address.deliveryAddress;

import com.ByteAndHeartDance.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Schema(description = "邮寄地址条件查询实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliveryAddressConditionEntity extends RequestConditionEntity {
  /** ID */
  @Schema(description = "ID")
  private Long id;

  /** 用户ID */
  @Schema(name = "userId", description = "用户ID")
  private Long userId;

  /** 收货人姓名 */
  @Schema(name = "receiverName", description = "收货人姓名")
  private String receiverName;

  /** 收货人电话 */
  @Schema(name = "receiverPhone", description = "收货人电话")
  private String receiverPhone;

  /** 省ID */
  @Schema(name = "provinceId", description = "省ID")
  private Long provinceId;

  /** 省 */
  @Schema(name = "province", description = "省")
  private String province;

  /** 市ID */
  @Schema(name = "cityId", description = "市ID")
  private Long cityId;

  /** 市 */
  @Schema(name = "city", description = "市")
  private String city;

  /** 区ID */
  @Schema(name = "districtId", description = "区ID")
  private Long districtId;

  /** 区 */
  @Schema(name = "district", description = "区")
  private String district;

  /** 邮政编码 */
  @Schema(name = "postCode", description = "邮政编码")
  private String postCode;

  /** 详细地址 */
  @Schema(name = "detailAddress", description = "详细地址")
  private String detailAddress;

  /** 是否默认地址 */
  @Schema(name = "addressIsDefault", description = "是否默认地址 1:是 0:否")
  private Boolean addressIsDefault;

  /** 创建人ID */
  @Schema(description = "创建人ID")
  private Long createUserId;

  /** 创建人名称 */
  @Schema(description = "创建人名称")
  private String createUserName;

  /** 创建日期 */
  @Schema(description = "创建时间")
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
