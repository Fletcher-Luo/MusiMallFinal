package com.ByteAndHeartDance.order.entity.address.deliveryAddress;

import com.ByteAndHeartDance.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "邮寄地址实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliveryAddressEntity extends BaseEntity {

  /** 用户ID */
  @NotEmpty(message = "用户ID不能为空")
  @Schema(name = "userId", description = "用户ID")
  private Long userId;

  /** 收货人姓名 */
  @NotEmpty(message = "收货人姓名不能为空")
  @Schema(name = "receiverName", description = "收货人姓名")
  private String receiverName;

  /** 收货人电话 */
  @NotEmpty(message = "收货人电话不能为空")
  @Schema(name = "receiverPhone", description = "收货人电话")
  private String receiverPhone;

  /** 省ID */
    @NotEmpty(message = "省ID不能为空")
    @Schema(name = "provinceId", description = "省ID")
    private Long provinceId;

    /** 省*/
    @NotEmpty(message = "省不能为空")
    @Schema(name = "province", description = "省")
    private String province;

    /** 市ID */
    @NotEmpty(message = "市ID不能为空")
    @Schema(name = "cityId", description = "市ID")
    private Long cityId;

    /** 市*/
    @NotEmpty(message = "市不能为空")
    @Schema(name = "city", description = "市")
    private String city;

    /** 区ID */
    @Schema(name = "districtId", description = "区ID")
    private Long districtId;

    /** 区*/

    @Schema(name = "district", description = "区")
    private String district;

    /** 邮政编码*/
    @Schema(name = "postCode", description = "邮政编码")
    private String postCode;

    /** 详细地址*/
    @NotEmpty(message = "详细地址不能为空")
    @Schema(name = "detailAddress", description = "详细地址")
    private String detailAddress;

    /** 是否默认地址*/
    @Schema(name = "addressIsDefault", description = "是否默认地址 1:是 0:否")
    private Boolean addressIsDefault;

}
