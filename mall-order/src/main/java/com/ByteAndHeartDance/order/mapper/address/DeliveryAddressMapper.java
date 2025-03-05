package com.ByteAndHeartDance.order.mapper.address;

import com.ByteAndHeartDance.order.entity.address.deliveryAddress.DeliveryAddressConditionEntity;
import com.ByteAndHeartDance.order.entity.address.deliveryAddress.DeliveryAddressEntity;
import com.ByteAndHeartDance.mapper.BaseMapper;

import java.util.List;

/** 地址服务 mapper */
public interface DeliveryAddressMapper
    extends BaseMapper<DeliveryAddressEntity, DeliveryAddressConditionEntity> {
  /**
   * 添加地址
   *
   * @param deliveryAddressEntity 订单信息
   * @return 结果(影响行数)
   */
  int insert(DeliveryAddressEntity deliveryAddressEntity);

  /**
   * 根据id查询地址信息
   *
   * @param id 地址id
   * @return 地址信息
   */
  DeliveryAddressEntity findById(Long id);

  /**
   * 批量查询地址信息
   *
   * @param ids id集合
   * @return 地址信息
   */
  List<DeliveryAddressEntity> findByIds(List<Long> ids);

  /**
   * 修改地址
   *
   * @param deliveryAddressEntity 地址信息
   * @return 结果(影响行数)
   */
  int update(DeliveryAddressEntity deliveryAddressEntity);



  /**
   * 批量删除地址
   *
   * @param ids id集合
   * @param entity 地址实体
   * @return 结果
   */
  int deleteByIds(List<Long> ids, DeliveryAddressEntity entity);
}
