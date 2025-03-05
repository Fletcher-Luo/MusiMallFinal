package com.ByteAndHeartDance.order.service.trade;

import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.mapper.BaseMapper;
import com.ByteAndHeartDance.order.entity.order.tradeItem.TradeItemConditionEntity;
import com.ByteAndHeartDance.order.entity.order.tradeItem.TradeItemEntity;
import com.ByteAndHeartDance.order.mapper.order.TradeItemMapper;
import com.ByteAndHeartDance.service.BaseService;
import com.ByteAndHeartDance.utils.AssertUtil;
import com.ByteAndHeartDance.utils.FillUserUtil;

import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** 订单明细查询、新增、删除 服务层 */
@Slf4j
@Service
public class TradeItemService extends BaseService<TradeItemEntity, TradeItemConditionEntity> {
  private final TradeItemMapper tradeItemMapper;

  public TradeItemService(
      TradeItemMapper tradeItemMapper) {
    this.tradeItemMapper = tradeItemMapper;
  }

  @Override
  protected BaseMapper getBaseMapper() {
    return tradeItemMapper;
  }

  /**
   * 查询订单明细信息
   *
   * @param id 订单明细ID
   * @return 订单明细信息
   */
  public TradeItemEntity findById(Long id) {
    return tradeItemMapper.findById(id);
  }

  /**
   * 根据条件分页查询订单明细列表（分页，直接调用）
   *
   * @param tradeItemConditionEntity 订单明细信息
   * @return 订单明细集合
   */
  public ResponsePageEntity<TradeItemEntity> searchByPage(
      TradeItemConditionEntity tradeItemConditionEntity) {
    if (Objects.isNull(tradeItemConditionEntity)) {
      tradeItemConditionEntity = new TradeItemConditionEntity();
    }
    return super.searchByPage(tradeItemConditionEntity); // 直接调用父类的分页查询
  }

  /**
   * 新增订单明细
   *
   * @param tradeItemEntity 订单明细信息
   * @return 结果
   */
  public int insert(TradeItemEntity tradeItemEntity) {
    return tradeItemMapper.insert(tradeItemEntity);
  }

  /**
   * 批量新增订单明细
   *
   * @param tradeItemEntities 订单明细信息集合
   * @return 结果
   */
  public int batchInsert(List<TradeItemEntity> tradeItemEntities) {
    return tradeItemMapper.batchInsert(tradeItemEntities);
  }

  /**
   * 修改订单明细
   *
   * @param tradeItemEntity 订单明细信息
   * @return 结果
   */
  public int update(TradeItemEntity tradeItemEntity) {
    AssertUtil.notNull(tradeItemEntity, "订单项不能为空");
    AssertUtil.notNull(tradeItemEntity.getId(), "订单项ID不能为空");
    return tradeItemMapper.update(tradeItemEntity);
  }

  /**
   * 批量删除订单明细对象
   *
   * @param ids 系统ID集合
   * @return 结果
   */
  public int deleteByIds(List<Long> ids) {
    List<TradeItemEntity> entities = tradeItemMapper.findByIds(ids);
    AssertUtil.notEmpty(entities, "订单明细不存在或已被删除");

    TradeItemEntity entity = new TradeItemEntity();
    FillUserUtil.fillUpdateUserInfo(entity);
    return tradeItemMapper.deleteByIds(ids, entity);
  }


  /**
   * 根据订单ID查询订单明细
   *
   * @param tradeId 订单ID
   * @return 订单明细集合
   */
    public List<TradeItemEntity> findByTradeId(Long tradeId) {
        return tradeItemMapper.findByTradeId(tradeId);
    }

    /**
     * 根据订单IDs查询订单明细数量
     *
     * @param tradeIds 订单IDs
     * @return 订单明细数量
     */
    public List<TradeItemEntity> findByTradeIds(List<Long> tradeIds) {
        return tradeItemMapper.findByTradeIds(tradeIds);

    }

}
