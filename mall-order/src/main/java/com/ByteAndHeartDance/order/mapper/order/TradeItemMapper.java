package com.ByteAndHeartDance.order.mapper.order;

import com.ByteAndHeartDance.mapper.BaseMapper;

import com.ByteAndHeartDance.order.entity.order.tradeItem.TradeItemConditionEntity;
import com.ByteAndHeartDance.order.entity.order.tradeItem.TradeItemEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 用户 mapper */
public interface TradeItemMapper extends BaseMapper<TradeItemEntity, TradeItemConditionEntity> {
  /**
   * 查询订单明细信息
   *
   * @param id 订单明细ID
   * @return 订单明细信息
   */
  TradeItemEntity findById(Long id);

  /**
   * 批量查询订单明细信息
   *
   * @param ids ID集合
   * @return 部门信息
   */
  List<TradeItemEntity> findByIds(List<Long> ids);

  /**
   * 添加订单明细
   *
   * @param tradeItemEntity 订单明细信息
   * @return 结果
   */
  int insert(TradeItemEntity tradeItemEntity);

  /**
   * 批量添加订单明细
   *
   * @param list 订单明细信息
   * @return 结果
   */
  int batchInsert(List<TradeItemEntity> list);

  /**
   * 修改订单明细
   *
   * @param tradeItemEntity 订单明细信息
   * @return 结果
   */
  int update(TradeItemEntity tradeItemEntity);

  /**
   * 批量删除订单明细
   *
   * @param ids id集合
   * @param entity 订单明细实体
   * @return 结果
   */
  int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") TradeItemEntity entity);

  /**
   * 根据交易订单ID查询交易订单明细
   *
   * @param tradeId 交易订单ID
   * @return 交易订单明细
   */
  List<TradeItemEntity> findByTradeId(Long tradeId);

  /**
   * 根据交易订单IDs查询交易订单明细
   *
   * @param tradeIds 交易订单IDs
   * @return 交易订单明细
   */
  List<TradeItemEntity> findByTradeIds(List<Long> tradeIds);
}
