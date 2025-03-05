package com.ByteAndHeartDance.shoppingcart.controller.shoppingcart;

import com.ByteAndHeartDance.entity.RequestPageEntity;
import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.shoppingcart.entity.DTO.request.AddShoppingCartDTO;
import com.ByteAndHeartDance.shoppingcart.entity.DTO.request.UpdateShoppingCartDTO;
import com.ByteAndHeartDance.shoppingcart.entity.ShoppingCartConditionEntity;
import com.ByteAndHeartDance.shoppingcart.entity.ShoppingCartEntity;
import com.ByteAndHeartDance.shoppingcart.entity.ShoppingCartWebEntity;
import com.ByteAndHeartDance.shoppingcart.service.ShoppingCartService;
import com.ByteAndHeartDance.utils.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@Tag(name = "购物车信息实体操作", description = "购物车信息操作接口")
@RestController
@Slf4j
@RequestMapping("/v1/shoppingCart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    // 构造函数注入
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    /**
     * 添加商品到购物车(创建一条购物车记录或更新购物车内该记录商品数量)
     * @param  addShoppingCartDTO 购物车插入信息(商品id，商品数量)
     * @return 影响行数
     */
    @PostMapping("/insertOrUpdate")
    @Operation(summary = "添加商品到购物车", description = "添加商品到购物车或更新购物车内该商品数量")
    public ApiResult<String> addProduct(@RequestBody @Validated AddShoppingCartDTO addShoppingCartDTO) {
        // 调用服务层方法添加商品到购物车
        return shoppingCartService.addShoppingCart(addShoppingCartDTO.getProductId(),addShoppingCartDTO.getQuantity());
    }

    /**
     * 删除一条购物车记录 (逻辑软删除)
     * @param id 商品id
     * @return 影响行数
     */
    @PutMapping("/deleteProduct/{id}")
    @Operation(summary = "删除购物车记录", description = "根据购物车记录ID删除(更新is_del列)一条购物车记录")
    public ApiResult<String> deleteProduct(@PathVariable Long id) {
        int num = shoppingCartService.deleteProduct(id);
        if (num > 0) {
            return new ApiResult<>(ApiResult.OK, "删除成功","影响行数:"+num );
        }
        return new ApiResult<>(ApiResult.OK, "删除失败","影响行数:"+num );
    }

    /**
     * 批量删除购物车记录 (逻辑软删除)
     * @return 影响行数
     */
    @Operation(summary = "批量删除购物车记录", description = "根据购物车记录ID列表批量删除购物车记录")
    @PutMapping("/deleteProducts")
    public ApiResult<String> deleteProducts(@RequestBody List<Long> ids) {
        int deletedCount = shoppingCartService.deleteByIds(ids);
        if (deletedCount > 0) {
            return new ApiResult<>(ApiResult.OK, "删除成功","影响行数:"+deletedCount);
        }
        return new ApiResult<>(ApiResult.OK, "删除失败","影响行数:"+deletedCount);

    }
    /**
     * 根据用户id删除所有购物车记录(即清空购物车)(逻辑软删除)
     * @return 影响行数
     */
    @PutMapping("/clearCart")
    @Operation(summary = "清空购物车", description = "根据用户ID清空购物车")
    public ApiResult<String> clearCart() {
        int deletedCount = shoppingCartService.clearCart();
        return new ApiResult(ApiResult.OK,"清空成功","影响行数:"+deletedCount);
    }

    /**
     * 修改购物车记录
     *
     * @Param id 购物车记录ID
     * @Param quantity 商品数量
     * @return 影响行数
     */
    @PutMapping("/updateShoppingCart")
    @Operation(summary = "修改购物车记录", description = "根据购物车记录ID修改购物车记录商品数量")
    public ApiResult<String> updateShoppingCart(@RequestBody @Validated UpdateShoppingCartDTO updateShoppingCartDTO) {
        int update =  shoppingCartService.updateShoppingCart(updateShoppingCartDTO.getId(),updateShoppingCartDTO.getQuantity());
        return new ApiResult(ApiResult.OK,"修改成功","影响行数:"+update);
    }


    /**
     * 通过id查询购物车信息
     *
     * @param id 系统ID
     * @return 购物车信息
     * @Description 根据购物车记录ID查询购物车信息
     * 场景：用于对选中的记录生成订单信息
     */
    @GetMapping("/findById/{id}")
    @Operation(summary = "根据购物车记录ID查询购物车信息", description = "根据购物车记录ID查询购物车信息")
    public ShoppingCartEntity findById(@PathVariable Long id) {
        return shoppingCartService.findById(id);
    }
    /**
     * 根据条件(用户ID)查询购物车中商品记录,分页返回
     *
     * @return 购物车信息列表
     */
    @PostMapping("/getShoppingCart")
    @Operation(summary = "查询当前用户购物车中商品记录", description = "根据用户ID查询当前用户购物车中商品记录")
    public ResponsePageEntity<ShoppingCartEntity> getShoppingCartCurrentUser(@RequestBody RequestPageEntity requestPageEntity) {
        // 返回分页面查询结果
        return shoppingCartService.searchByPageCurrentUser(requestPageEntity);
    }

    /**
     * 根据条件(如用户ID)查询购物车中商品记录,分页返回
     * @param shoppingCartConditionEntity 购物车条件实体
     * @return 购物车信息列表
     * @Description 更通用的查询方法
     * 场景；用于对选中的记录生成订单信息
     *
     */
    @PostMapping("/getShoppingCartByCondition")
    @Operation(summary = "根据条件查询购物车中商品记录", description = "根据条件查询购物车中商品记录")
    public ResponsePageEntity<ShoppingCartEntity> getShoppingCart(@RequestBody ShoppingCartConditionEntity shoppingCartConditionEntity) {
        // 返回分页面查询结果
        return shoppingCartService.searchByPage(shoppingCartConditionEntity);
    }

    /**
     * 根据购物车记录ID列表查询购物车金额状态
     * @param ids 购物车记录ID列表
     * @return 购物车金额状态
     * @Description
     * 场景；实时对选中的购物车记录进行查询，计算金额
     * 本应该结合优惠券模块进行计算
     */
    @PostMapping("/getShoppingCartBySelectedIds")
    @Operation(summary = "根据购物车记录ID列表查询购物车中商品记录金额状态", description = "根据购物车记录ID列表查询购物车中商品记录金额状态")
    public ShoppingCartWebEntity searchBySelectedIds(@RequestBody ArrayList<Long> ids) {
        return shoppingCartService.searchBySelectedIds(ids);
    }
}
