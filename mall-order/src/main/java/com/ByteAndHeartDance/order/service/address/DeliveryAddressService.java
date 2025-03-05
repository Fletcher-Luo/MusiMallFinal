package com.ByteAndHeartDance.order.service.address;

import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.entity.auth.JwtUserEntity;
import com.ByteAndHeartDance.mapper.BaseMapper;
import com.ByteAndHeartDance.order.entity.address.deliveryAddress.DeliveryAddressConditionEntity;
import com.ByteAndHeartDance.order.entity.address.deliveryAddress.DeliveryAddressEntity;
import com.ByteAndHeartDance.order.mapper.address.DeliveryAddressMapper;
import com.ByteAndHeartDance.service.BaseService;
import com.ByteAndHeartDance.utils.AssertUtil;
import com.ByteAndHeartDance.utils.FillUserUtil;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** 地址查询、新增、删除 服务层 */
@Slf4j
@Service
public class DeliveryAddressService
    extends BaseService<DeliveryAddressEntity, DeliveryAddressConditionEntity> {
  private final DeliveryAddressMapper deliveryAddressMapper;

  public DeliveryAddressService(DeliveryAddressMapper deliveryAddressMapper) {
    this.deliveryAddressMapper = deliveryAddressMapper;
  }

  @Override
  protected BaseMapper getBaseMapper() {
    return deliveryAddressMapper;
  }

  /**
   * 根据条件分页查询地址列表（分页，直接调用）
   *
   * @param deliveryAddressConditionEntity 地址信息
   * @return 地址集合
   */
  public ResponsePageEntity<DeliveryAddressEntity> searchByPage(
      DeliveryAddressConditionEntity deliveryAddressConditionEntity) {
    // 如果查询条件为空，创建一个新的
    if (Objects.isNull(deliveryAddressConditionEntity)) {
      deliveryAddressConditionEntity = new DeliveryAddressConditionEntity();
    }
    return super.searchByPage(deliveryAddressConditionEntity); // 直接调用父类的分页查询
  }

  /**
   * 根据条件查询当前用户的地址列表
   *
   * @param deliveryAddressConditionEntity 条件
   * @return 地址列表
   */
  public ResponsePageEntity<DeliveryAddressEntity> searchByPageCurrentUser(
      DeliveryAddressConditionEntity deliveryAddressConditionEntity) {
    // 获取当前用户信息
    JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
    AssertUtil.notNull(currentUserInfo, "请先登录");
    // 如果查询条件为空，创建一个新的
    if (Objects.isNull(deliveryAddressConditionEntity)) {
      deliveryAddressConditionEntity = new DeliveryAddressConditionEntity();
    }
    // 设置当前用户ID
    deliveryAddressConditionEntity.setUserId(currentUserInfo.getId());
    // 调用分页查询
    return searchByPage(deliveryAddressConditionEntity);
  }

  /**
   * 根据条件查询地址列表（不分页）
   *
   * @param deliveryAddressConditionEntity 查询条件
   * @return 地址集合
   */
  public List<DeliveryAddressEntity> searchByCondition(
      DeliveryAddressConditionEntity deliveryAddressConditionEntity) {
    deliveryAddressConditionEntity.setPageSize(0); // 不分页
    return deliveryAddressMapper.searchByCondition(deliveryAddressConditionEntity);
  }

  /**
   * 根据地址ID查询地址
   *
   * @param id 地址ID
   * @return 地址信息
   */
  public DeliveryAddressEntity findById(Long id) {
    return deliveryAddressMapper.findById(id);
  }

  /**
   * 根据条件查询地址数量
   *
   * @param deliveryAddressConditionEntity 查询条件
   * @return 数量
   */
  public int searchCount(DeliveryAddressConditionEntity deliveryAddressConditionEntity) {
    return deliveryAddressMapper.searchCount(deliveryAddressConditionEntity);
  }

  /**
   * 修改地址
   *
   * @param deliveryAddressEntity 地址信息
   * @return 结果
   */
  public int update(DeliveryAddressEntity deliveryAddressEntity) {
    AssertUtil.notNull(deliveryAddressEntity, "地址信息不能为空");
    AssertUtil.notNull(deliveryAddressEntity.getId(), "地址ID不能为空");
    return deliveryAddressMapper.update(deliveryAddressEntity);
  }

  /**
   * 批量删除地址对象
   *
   * @param ids 系统ID集合
   * @return 结果
   */
  public int deleteByIds(List<Long> ids) {
    List<DeliveryAddressEntity> entities = deliveryAddressMapper.findByIds(ids);
    AssertUtil.notEmpty(entities, "地址不存在或已被删除");
    // 填充用户信息（只能删除当前用户的）
    DeliveryAddressEntity entity = new DeliveryAddressEntity();
    FillUserUtil.fillUpdateUserInfo(entity);
    return deliveryAddressMapper.deleteByIds(ids, entity);
  }

  /**
   * 新增地址
   *
   * @param deliveryAddressEntity 地址信息
   * @return 结果
   */
  public int insert(DeliveryAddressEntity deliveryAddressEntity) {
    AssertUtil.notNull(deliveryAddressEntity, "地址信息不能为空");
    // 填充用户信息
    FillUserUtil.fillCreateUserInfo(deliveryAddressEntity);
    if(deliveryAddressEntity.getUserId()==null){
      JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
      deliveryAddressEntity.setUserId(currentUserInfo.getId());
    }
    // 默认地址都是非默认
    deliveryAddressEntity.setAddressIsDefault(false);
    return deliveryAddressMapper.insert(deliveryAddressEntity);
  }

  /**
   * 设为默认地址（只能存在一个默认地址）
   *
   * @param id 地址ID
   * @return 结果
   */
  public int setDefault(Long id) {
    AssertUtil.notNull(id, "地址ID不能为空");

    // 获取当前用户信息
    JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
    AssertUtil.notNull(currentUserInfo, "请先登录");
    // 查询当前地址信息
    DeliveryAddressEntity addressNeedToUpdate = findById(id);
    AssertUtil.notNull(addressNeedToUpdate, "地址不存在");
    // 查询当前用户的地址列表
    DeliveryAddressConditionEntity conditionEntity = new DeliveryAddressConditionEntity();
    conditionEntity.setUserId(currentUserInfo.getId());
    List<DeliveryAddressEntity> addressList = searchByCondition(conditionEntity);
    // 遍历地址列表，将其他地址设为非默认
    for (DeliveryAddressEntity address : addressList) {
      // 等于id的设为默认地址，其他的设为非默认地址
      log.info("address.getId()={},id={},ret={}", address.getId(), id, address.getId().equals(id));
      address.setAddressIsDefault(address.getId().equals(id));
      deliveryAddressMapper.update(address);
    }
    return 1;
  }

  /**
   * 查询当前用户的默认地址ID
   *
   * @param userId 用户ID
   * @return 默认地址ID
   */
  public Long selectDefaultAddressId(Long userId) {
    DeliveryAddressConditionEntity conditionEntity = new DeliveryAddressConditionEntity();
    conditionEntity.setUserId(userId);
    conditionEntity.setAddressIsDefault(true);
    List<DeliveryAddressEntity> addressList = searchByCondition(conditionEntity);
    if (addressList.size() > 0) {
      return addressList.get(0).getId();
    }
    return null;
  }

  /**
   * 检查地址是否属于当前用户
   *
   * @param userId 用户ID
   * @param deliveryAddressId 地址ID
   * @return 是否属于当前用户
   */
  public Boolean checkAddress(Long userId, Long deliveryAddressId) {
    DeliveryAddressEntity deliveryAddressEntity = findById(deliveryAddressId);
    if (Objects.isNull(deliveryAddressEntity)) {
      return false;
    } else {
      return deliveryAddressEntity.getUserId().equals(userId);
    }
  }

  /**
   * 查询默认地址
   * @return 默认地址
   */
    public DeliveryAddressEntity findDefault() {
        JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
        AssertUtil.notNull(currentUserInfo, "请先登录");
        DeliveryAddressConditionEntity conditionEntity = new DeliveryAddressConditionEntity();
        conditionEntity.setUserId(currentUserInfo.getId());
        conditionEntity.setAddressIsDefault(true);
        List<DeliveryAddressEntity> addressList = searchByCondition(conditionEntity);
        if (addressList.size() > 0) {
            return addressList.get(0);
        }
        return null;
    }
}
