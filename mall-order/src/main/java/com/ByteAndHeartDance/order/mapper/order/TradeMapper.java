package com.ByteAndHeartDance.order.mapper.order;



import com.ByteAndHeartDance.mapper.BaseMapper;
import com.ByteAndHeartDance.order.entity.order.trade.TradeConditionEntity;
import com.ByteAndHeartDance.order.entity.order.trade.TradeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 用户 mapper
 */
public interface TradeMapper extends BaseMapper<TradeEntity, TradeConditionEntity> {
    /**
     * 查询订单信息
     *
     * @param id 订单ID
     * @return 订单信息
     */
    TradeEntity findById(Long id);

    /**
     * 批量查询订单信息
     * @param ids 订单ID集合
     * @return 订单实体集合
     */
    List<TradeEntity> findByIds(List<Long> ids);


    /**
     * 根据条件查询订单列表
     * @param tradeConditionEntity 订单信息
     * @return 订单集合
     */
    List<TradeEntity> searchByCondition(TradeConditionEntity tradeConditionEntity);

    /**
     * 修改订单
     *
     * @param tradeEntity 订单信息
     * @return 结果
     */
    int update(TradeEntity tradeEntity);

    /**
     * 批量删除订单
     *
     * @param ids    id集合
     * @param entity 订单实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") TradeEntity entity);


    /**
     * 添加订单
     *
     * @param tradeEntity 订单信息
     * @return 结果(影响行数)
     */
    long insert(TradeEntity tradeEntity);





}
