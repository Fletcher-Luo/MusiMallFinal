package com.ByteAndHeartDance.shoppingcart.mapper.shoppingCart;

import com.ByteAndHeartDance.mapper.BaseMapper;
import com.ByteAndHeartDance.shoppingcart.entity.ShoppingCartConditionEntity;
import com.ByteAndHeartDance.shoppingcart.entity.ShoppingCartEntity;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 购物车信息记录表(ShoppingCart)表数据库访问层
 */
public interface ShoppingCartMapper extends BaseMapper<ShoppingCartEntity, ShoppingCartConditionEntity> {
    /**
     * 向购物车信息记录表中添加一条购物车信息记录
     * @param shoppingCartEntity
     */
    int insertShoppingCartRecord(@Valid ShoppingCartEntity shoppingCartEntity);

    /**
     * 根据ID查询购物车记录  
     * @param id
     * @return
     * @description  根据ID查询定位购物车记录:
     * 1. 辅助更新和删除操作
     * 2. springdata Optional
     */
    ShoppingCartEntity findById(Long id);

    /**
     * 修改购物车
     *
     * @param shoppingCartEntity 购物车信息
     * @return 结果
     */
    int update(ShoppingCartEntity shoppingCartEntity);

    /**
     * 批量删除购物车
     *
     * @param ids id集合
     * @param entity 购物车实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") ShoppingCartEntity entity);

    /**
     * 批量查询购物车信息
     *
     * @param ids ID集合
     * @return 购物车信息
     */
    List<ShoppingCartEntity> findByIds(List<Long> ids);
}
