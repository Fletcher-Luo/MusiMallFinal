package com.ByteAndHeartDance.shoppingcart.service;

import com.ByteAndHeartDance.entity.RequestPageEntity;
import com.ByteAndHeartDance.entity.auth.JwtUserEntity;
import com.ByteAndHeartDance.helper.IdGenerateHelper;
import com.ByteAndHeartDance.mapper.BaseMapper;
import com.ByteAndHeartDance.shoppingcart.entity.ShoppingCartConditionEntity;
import com.ByteAndHeartDance.shoppingcart.entity.ShoppingCartEntity;
import com.ByteAndHeartDance.shoppingcart.entity.ShoppingCartProductWebEntity;
import com.ByteAndHeartDance.shoppingcart.entity.ShoppingCartWebEntity;
import com.ByteAndHeartDance.shoppingcart.helper.UserProductHelper;
import com.ByteAndHeartDance.service.BaseService;
import com.ByteAndHeartDance.shoppingcart.util.JedisUtil;
import com.ByteAndHeartDance.utils.ApiResult;
import org.springframework.util.CollectionUtils;
import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.shoppingcart.mapper.shoppingCart.ShoppingCartMapper;
import com.ByteAndHeartDance.utils.AssertUtil;
import com.ByteAndHeartDance.utils.FillUserUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ShoppingCartService extends BaseService<ShoppingCartEntity, ShoppingCartConditionEntity> {
    // 只在查询时用到，避免重复调用远程接口进行查询
    private final JedisUtil jedisUtil;
    private final String PRODUCT_KEY = "shoppingCart:product:";
    private final UserProductHelper userProductHelper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final IdGenerateHelper idGenerateHelper;
    // 构造函数注入
    public ShoppingCartService(UserProductHelper userProductHelper, ShoppingCartMapper shoppingCartMapper, IdGenerateHelper idGenerateHelper, JedisUtil jedisUtil) {
        this.userProductHelper = userProductHelper;
        this.shoppingCartMapper = shoppingCartMapper;
        this.idGenerateHelper = idGenerateHelper;
        this.jedisUtil = jedisUtil;
    }


    @Override
    protected BaseMapper getBaseMapper() {
        return shoppingCartMapper;
    }
    /**
     * 分配购物车记录ID
     *
     * @return 购物车记录ID
     */
    public Long createShoppingCartId() {
        Long shoppingCartId = idGenerateHelper.nextId();
        return shoppingCartId;
    }

    /**
     * 新增购物车商品信息
     *
     * @param shoppingCartEntity 购物车信息
     * @return 结果
     */
    public int insert(ShoppingCartEntity shoppingCartEntity) {
        // 检验商品是否存在
        userProductHelper.checkParam(shoppingCartEntity);
        // 分配购物车记录ID
        shoppingCartEntity.setId(createShoppingCartId());
        // 填充创建用户信息
        FillUserUtil.fillCreateUserInfo(shoppingCartEntity);
        // 填充更新用户信息
        FillUserUtil.fillUpdateUserInfo(shoppingCartEntity);
        return shoppingCartMapper.insertShoppingCartRecord(shoppingCartEntity);
    }
    /**
     * 添加购物车
     *
     * @param productId 商品id
     * @param quantity  商品数量
     * @return
     */
    public ApiResult<String> addShoppingCart(Long productId, Integer quantity) {
        ShoppingCartEntity shoppingCartEntity = new ShoppingCartEntity();
        // 填充商品信息
        shoppingCartEntity.setProductId(productId);
        shoppingCartEntity.setQuantity(quantity);
        // 获取用户并设置ID
        shoppingCartEntity.setUserId(FillUserUtil.getCurrentUserInfo().getId());
        // 不做校验库存
        // tradeService.checkStock(Lists.newArrayList(shoppingCartEntity));
        // 创建购物车查询条件实体
        ShoppingCartConditionEntity shoppingCartConditionEntity = new ShoppingCartConditionEntity();
        shoppingCartConditionEntity.setUserId(shoppingCartEntity.getUserId());
        shoppingCartConditionEntity.setProductId(shoppingCartEntity.getProductId());
        // 调用mapper查询购物车信息 使用用户ID和商品ID查询购物车信息
        List<ShoppingCartEntity> shoppingCartEntities = shoppingCartMapper.searchByCondition(shoppingCartConditionEntity);
        // 该用户的购物车中已存在该商品
        if (!CollectionUtils.isEmpty(shoppingCartEntities)) {
            // 获取购物车内该商品记录
            ShoppingCartEntity oldShoppingCartEntity = shoppingCartEntities.get(0);
            // 设置购物车内该商品数量信息
            oldShoppingCartEntity.setQuantity(oldShoppingCartEntity.getQuantity() + shoppingCartEntity.getQuantity());
            // 设置购物车内该商品更新用户信息
            FillUserUtil.fillUpdateUserInfo(oldShoppingCartEntity);
            int update = shoppingCartMapper.update(oldShoppingCartEntity);
            if (update > 0) {
                // 更新购物车
                return  new ApiResult<>(ApiResult.OK,"更新购物车成功","影响行数："+update);
            }else {
                return  new ApiResult<>(ApiResult.OK,"更新购物车失败","影响行数："+update);
            }
        } else {
            int insert = insert(shoppingCartEntity);
            // 加入购物车
            if (insert > 0) {
                return  new ApiResult<>(ApiResult.OK,"加入购物车成功","影响行数："+insert);
            }  else {
                return  new ApiResult<>(ApiResult.OK,"加入购物车失败","影响行数："+insert);
            }
        }
    }
    /**
     * 批量删除购物车
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        // 查询购物车信息
        List<ShoppingCartEntity> entities = shoppingCartMapper.findByIds(ids);
        // 确保查询到的购物车记录不为空，如果为空，抛出异常
        AssertUtil.notEmpty(entities, "购物车信息不存在或已被删除");
        ShoppingCartEntity entity = new ShoppingCartEntity();
        // 设置删除用户信息
        FillUserUtil.fillUpdateUserInfo(entity);
        // 调用mapper层删除购物车信息
        return shoppingCartMapper.deleteByIds(ids, entity);
    }
    /**
     * 删除一条购物车信息
     * @param id
     * @return
     */
    public int deleteProduct(Long id) {
        // 获取当前用户信息
        JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
        AssertUtil.notNull(currentUserInfo,"请先登录");

        ShoppingCartEntity shoppingCartEntity = findById(id);
        AssertUtil.notNull(shoppingCartEntity,"购物车信息已被删除");
        // 填充删除用户信息
        FillUserUtil.fillUpdateUserInfo(shoppingCartEntity);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(shoppingCartEntity.getId());
        return shoppingCartMapper.deleteByIds(ids, shoppingCartEntity);
    }
    /**
     * 清空购物车
     *
     * @return
     */
    public int clearCart() {
        // 获取当前用户信息
        JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
        AssertUtil.notNull(currentUserInfo,"请先登录");
        Long currentUserInfoId = currentUserInfo.getId();
        ShoppingCartConditionEntity shoppingCartConditionEntity = new ShoppingCartConditionEntity();
        shoppingCartConditionEntity.setId(currentUserInfoId);
        // 调用searchByPageCurrentUser方法获取当前用户的购物车列表
        ResponsePageEntity<ShoppingCartEntity> shoppingCartEntities = searchByPageCurrentUser(shoppingCartConditionEntity);
        // 如果购物车列表为空，直接返回true
        if (CollectionUtils.isEmpty(shoppingCartEntities.getData())) {
            return 0;
        }
        // 获取购物车列表中的商品ID列表
        List<Long> idList = shoppingCartEntities.getData().stream().map(ShoppingCartEntity::getId).toList();
        int num = deleteByIds(idList);
        return num;
    }

    /**
     * 修改购物车信息商品数量
     * @param   id 购物车记录ID
     * @param   quantity 商品数量
     *
     * @return  影响行数
     *
     */
    public int updateShoppingCart( Long id, Integer quantity) {
        ShoppingCartEntity shoppingCartEntity = findById(id);
        AssertUtil.notNull(shoppingCartEntity, "购物车信息不存在或已被删除");
        shoppingCartEntity.setQuantity(quantity);

        /** 调用mapper修改购物车信息 */
        return shoppingCartMapper.update(shoppingCartEntity);
    }
    /**
     * 修改购物车
     *
     * @param shoppingCartEntity 购物车信息
     * @return 结果
     */
    public int update(ShoppingCartEntity shoppingCartEntity) {
        AssertUtil.notNull(shoppingCartEntity.getId(), "id不能为空");
        AssertUtil.notNull(shoppingCartEntity.getQuantity(), "quantity不能为空");
        AssertUtil.isTrue(shoppingCartEntity.getQuantity() > 0, "quantity必须大于0");
        /** 填充修改用户信息 */
        // 方法中进行了登录状态的校验，如果未登录则会抛出异常
        FillUserUtil.fillUpdateUserInfo(shoppingCartEntity);
        return shoppingCartMapper.update(shoppingCartEntity);
    }
    /**
     * 根据条件搜索购物车商品列表
     *
     * @param shoppingCartConditionEntity 条件
     * @return 购物车商品列表
     * @Description 根据条件搜索购物车商品列表，获取商品列表信息
     * 场景：选中信息计算最终支付金额
     *
     */
    public ShoppingCartWebEntity getShoppingCartProduct(ShoppingCartConditionEntity shoppingCartConditionEntity) {
        // 购物车信息
        ShoppingCartWebEntity shoppingCartWebEntity = new ShoppingCartWebEntity();
        // 用户id和购物车页码
        shoppingCartConditionEntity.setUserId(Objects.requireNonNull(FillUserUtil.getCurrentUserInfo()).getId());
        shoppingCartConditionEntity.setPageSize(0);
        // 查询购物车信息
        ResponsePageEntity<ShoppingCartEntity> responsePageEntity = super.searchByPage(shoppingCartConditionEntity);
        //填充商品信息
        List<ShoppingCartEntity> data = responsePageEntity.getData();
        List<ShoppingCartProductWebEntity> shoppingCartProductWebEntities = convertShoppingCartProductWebEntity(responsePageEntity);
        //计算每个商品总金额
        calcTotalAmount(shoppingCartWebEntity, shoppingCartProductWebEntities);

        return shoppingCartWebEntity;
    }
    /**
     * 查询购物车信息
     *
     * @param id 购物车ID
     * @return 一条购物车信息记录
     */
    public ShoppingCartEntity findById(Long id) {
        ShoppingCartEntity shoppingCartEntity = shoppingCartMapper.findById(id);
        // 确保查询到的购物车记录不为空，如果为空，抛出异常
        AssertUtil.notNull(shoppingCartEntity, "购物车信息已被删除");
        shoppingCartEntity.setCreateTime(new Date(shoppingCartEntity.getCreateTime().getTime()));
        shoppingCartEntity.setUpdateTime(new Date(shoppingCartEntity.getUpdateTime().getTime()));
        List<ShoppingCartEntity> objects = new ArrayList<>();
        objects.add(shoppingCartEntity);
        // 填充用户商品信息
        userProductHelper.fillUserProductInfo(objects);
        ShoppingCartEntity shoppingCartEntity1 = objects.get(0);
        shoppingCartEntity1.setTotalAmount(getAmount(shoppingCartEntity1.getPrice(), shoppingCartEntity.getQuantity()));
        return shoppingCartEntity;
    }



    /**
     * 根据条件分页查询购物车列表
     * 通常情况下，查询方法需要查询删除状态为未删除的记录---->查找未删除的记录交给前端设置是否合理？ 这样还可以进行已删除记录的查询
     *
     * @param shoppingCartConditionEntity 购物车查询信息
     * @return 购物车集合
     */
    public ResponsePageEntity<ShoppingCartEntity> searchByPage(ShoppingCartConditionEntity shoppingCartConditionEntity) {
        // 确保查询对象不为空
        if (shoppingCartConditionEntity == null) {
            shoppingCartConditionEntity = new ShoppingCartConditionEntity();
        }
//        shoppingCartConditionEntity.setIsDel(0);
        // 查询分页购物车列表
        ResponsePageEntity<ShoppingCartEntity> responsePageEntity = super.searchByPage(shoppingCartConditionEntity);
        // 填充购物车信息列表，填充用户信息和商品信息，因为查询出来的是商品ID和用户ID，其他信息需要展示
        userProductHelper.fillUserProductInfo(responsePageEntity.getData());
        // 如果列表非空，计算每个购物车记录的总金额
        if (!CollectionUtils.isEmpty(responsePageEntity.getData())) {
            for (ShoppingCartEntity shoppingCartEntity : responsePageEntity.getData()) {
                shoppingCartEntity.setTotalAmount(new BigDecimal(shoppingCartEntity.getQuantity()).multiply(shoppingCartEntity.getPrice()));
            }
        }
        return responsePageEntity;
    }

    /**
     * 根据购物车记录ID列表查询购物车中商品记录的结算价格信息
     * @param ids
     * @return
     */
    public ShoppingCartWebEntity searchBySelectedIds(ArrayList<Long> ids) {
        // 若未选中商品，则返回空对象
        if (ids == null||ids.isEmpty()) {
            return new ShoppingCartWebEntity();
        }
        ShoppingCartConditionEntity shoppingCartConditionEntity = new ShoppingCartConditionEntity();
        shoppingCartConditionEntity.setIdList(ids);
        // 查询分页购物车列表
        ResponsePageEntity<ShoppingCartEntity> responsePageEntity = super.searchByPage(shoppingCartConditionEntity);

        // 填充购物车信息列表，填充用户信息和商品信息，因为查询出来的是商品ID和用户ID，其他信息需要展示
        userProductHelper.fillUserProductInfo(responsePageEntity.getData());
        ShoppingCartWebEntity shoppingCartWebEntity = new ShoppingCartWebEntity();

        // 确保查询到的购物车记录不为空，如果为空，抛出异常
        AssertUtil.notEmpty(responsePageEntity.getData(), "选中的购物车信息已被删除");
        // 如果列表非空，计算每个购物车记录的总金额
        if (!CollectionUtils.isEmpty(responsePageEntity.getData())) {
            calcTotalAmount(shoppingCartWebEntity,convertShoppingCartProductWebEntity(responsePageEntity));
        }
        shoppingCartWebEntity.setFinalMoney(shoppingCartWebEntity.getTotalMoney().subtract(shoppingCartWebEntity.getSubtractMoney()));
        return shoppingCartWebEntity;
    }

    /**
     * 获取当前用户的购物车列表
     *
     * @return
     *
     */
    public ResponsePageEntity<ShoppingCartEntity> searchByPageCurrentUser(RequestPageEntity requestPageEntity) {
        // 获取当前用户信息
        JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfo();
        AssertUtil.notNull(currentUserInfo,"请先登录");
        // 获取用户ID
        Long userId = currentUserInfo.getId();
        // 创建购物车查询条件实体
        ShoppingCartConditionEntity shoppingCartConditionEntity = new ShoppingCartConditionEntity();
        shoppingCartConditionEntity.setPageNo(requestPageEntity.getPageNo());
        shoppingCartConditionEntity.setPageSize(requestPageEntity.getPageSize());
        shoppingCartConditionEntity.setSortField(requestPageEntity.getSortField());
        // 设置用户ID
        shoppingCartConditionEntity.setUserId(userId);
        return searchByPage(shoppingCartConditionEntity);
    }


    /**
     * 计算一条记录的金额
     * @param price 价格
     * @param quantity 数量
     * @return 金额
     */
    private BigDecimal getAmount(BigDecimal price, int quantity) {
        return price.multiply(new BigDecimal(quantity));
    }


    /**
     * 计算无优惠最终价格
     * @param shoppingCartWebEntity
     * @param shoppingCartProductWebEntities
     */
    private void calcTotalAmount(ShoppingCartWebEntity shoppingCartWebEntity, List<ShoppingCartProductWebEntity> shoppingCartProductWebEntities) {
        // 总金额初始化
        BigDecimal totalMoney = BigDecimal.ZERO;
        //  遍历购物车商品信息列表，计算总金额
        for (ShoppingCartProductWebEntity shoppingCartProductWebEntity : shoppingCartProductWebEntities) {
            BigDecimal totalAmount = getAmount(shoppingCartProductWebEntity.getPrice(), shoppingCartProductWebEntity.getQuantity());
            shoppingCartProductWebEntity.setTotalAmount(totalAmount);
            shoppingCartProductWebEntity.setPayAmount(totalAmount);
            shoppingCartProductWebEntity.setPayPrice(shoppingCartProductWebEntity.getPrice());
            totalMoney = totalMoney.add(totalAmount);
        }
        shoppingCartWebEntity.setTotalMoney(totalMoney);
    }

    /**
     * 转换购物车商品信息列表为购物车商品信息传输列表
     * @param responsePageEntity
     * @return
     */
    private List<ShoppingCartProductWebEntity> convertShoppingCartProductWebEntity(ResponsePageEntity<ShoppingCartEntity> responsePageEntity) {
        // 购物车信息为空，直接返回空列表
        if (CollectionUtils.isEmpty(responsePageEntity.getData())) {
            return Collections.emptyList();
        }
        // 转换购物车商品信息列表为购物车商品信息传输列表
        return responsePageEntity.getData().stream().map(x -> {
            ShoppingCartProductWebEntity shoppingCartProductWebEntity = new ShoppingCartProductWebEntity();
            shoppingCartProductWebEntity.setId(x.getId());
            shoppingCartProductWebEntity.setProductId(x.getProductId());
            shoppingCartProductWebEntity.setProductName(x.getProductName());
            shoppingCartProductWebEntity.setModel(x.getModel());
            shoppingCartProductWebEntity.setPrice(x.getPrice());
            shoppingCartProductWebEntity.setQuantity(x.getQuantity());
            shoppingCartProductWebEntity.setCover(x.getCover());
            shoppingCartProductWebEntity.setCreateTime(x.getCreateTime());
            shoppingCartProductWebEntity.setTotalAmount(x.getTotalAmount());
            shoppingCartProductWebEntity.setStock(x.getStock());
            return shoppingCartProductWebEntity;
        }).collect(Collectors.toList());
    }
}
