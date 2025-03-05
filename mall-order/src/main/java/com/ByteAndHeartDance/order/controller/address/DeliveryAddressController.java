package com.ByteAndHeartDance.order.controller.address;

import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.order.entity.address.deliveryAddress.DeliveryAddressConditionEntity;
import com.ByteAndHeartDance.order.entity.address.deliveryAddress.DeliveryAddressEntity;
import com.ByteAndHeartDance.order.service.address.DeliveryAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "地址操作", description = "地址操作接口")
@RestController
@RequestMapping("/v1/deliveryAddress")
@Validated
@Slf4j
public class DeliveryAddressController {
  private final DeliveryAddressService deliveryAddressService;

  public DeliveryAddressController(DeliveryAddressService deliveryAddressService) {
    this.deliveryAddressService = deliveryAddressService;
  }

  /**
   * 通过id查询地址信息
   *
   * @param id 系统ID
   * @return 地址信息
   */
  @Operation(summary = "通过id查询地址信息", description = "通过id查询地址信息")
  @GetMapping("/findById")
  public DeliveryAddressEntity findById(Long id) {
    return deliveryAddressService.findById(id);
  }

  /**
   * 查询当前用户的默认地址信息
   * @return 地址信息
   */
    @Operation(summary = "查询当前用户的默认地址信息", description = "查询当前用户的默认地址信息")
    @GetMapping("/findDefault")
    public DeliveryAddressEntity findDefault() {
        return deliveryAddressService.findDefault();
    }

  /**
   * 根据条件分页查询地址列表
   *
   * @param deliveryAddressConditionEntity 条件
   * @return 地址列表
   */
  @Operation(summary = "根据条件分页查询地址列表", description = "根据条件查询地址列表")
  @PostMapping("/searchByPage")
  public ResponsePageEntity<DeliveryAddressEntity> searchByPage(
      @RequestBody DeliveryAddressConditionEntity deliveryAddressConditionEntity) {
    return deliveryAddressService.searchByPage(deliveryAddressConditionEntity);
  }
  /**
   * 根据条件分页查询当前用户的订单列表
   *
   * @param deliveryAddressConditionEntity 条件
   * @return 地址列表
   */
  @Operation(summary = "根据条件分页查询当前用户的地址列表", description = "根据条件查询当前用户的地址列表")
  @PostMapping("/searchByPageCurrentUser")
  public ResponsePageEntity<DeliveryAddressEntity> searchByPageCurrentUser(
          @RequestBody DeliveryAddressConditionEntity deliveryAddressConditionEntity) {
    return deliveryAddressService.searchByPageCurrentUser(deliveryAddressConditionEntity);
  }

  /**
   * 添加地址（添加时，均为非默认地址）
   *
   * @param deliveryAddressEntity 地址信息
   * @return 结果(影响行数)
   */
  @Operation(summary = "添加地址", description = "添加地址")
  @PostMapping("/add")
  public int add(@RequestBody DeliveryAddressEntity deliveryAddressEntity) {
    return deliveryAddressService.insert(deliveryAddressEntity);
  }

  /**
   * 修改地址
   *
   * @param deliveryAddressEntity 地址信息
   * @return 结果(影响行数)
   */
  @Operation(summary = "修改地址", description = "修改地址")
  @PostMapping("/update")
  public int update(@RequestBody DeliveryAddressEntity deliveryAddressEntity) {
    return deliveryAddressService.update(deliveryAddressEntity);
  }

  /**
   * 批量删除地址
   *
   * @param ids id集合
   * @return 结果
   */
  @Operation(summary = "批量删除地址", description = "批量删除地址")
  @PostMapping("/deleteByIds")
  public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
    return deliveryAddressService.deleteByIds(ids);
  }

  /**
   * 设置默认地址
   *
   * @param id 地址ID
   * @return 结果
   */
  @Operation(summary = "设置默认地址", description = "设置默认地址")
  @PostMapping("/setDefault")
  public int setDefault(@NotNull Long id) {
    return deliveryAddressService.setDefault(id);
  }
}
