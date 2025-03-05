package com.ByteAndHeartDance.order.controller.trade;


import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.order.entity.order.tradeItem.TradeItemConditionEntity;
import com.ByteAndHeartDance.order.entity.order.tradeItem.TradeItemEntity;
import com.ByteAndHeartDance.order.service.trade.TradeItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "订单明细操作", description = "订单明细操作接口")
@RestController
@RequestMapping("/v1/tradeItem")
@Validated
@Slf4j
public class TradeItemController {
    private final TradeItemService tradeItemService;

    public TradeItemController(TradeItemService tradeItemService) {
        this.tradeItemService = tradeItemService;
    }

    /**
     * 通过id查询订单明细信息
     *
     * @param id 系统ID
     * @return 订单明细信息
     */
    @Operation(summary = "通过id查询订单明细信息", description = "通过id查询订单明细信息")
    @GetMapping("/findById")
    public TradeItemEntity findById(Long id) {
        return tradeItemService.findById(id);
    }

    /**
     * 根据条件分页查询订单明细列表
     *
     * @param tradeItemConditionEntity 条件
     * @return 订单明细列表
     */
    @Operation(summary = "根据条件分页查询订单明细列表", description = "根据条件查询订单明细列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<TradeItemEntity> searchByPage(
            @RequestBody TradeItemConditionEntity tradeItemConditionEntity) {
        return tradeItemService.searchByPage(tradeItemConditionEntity);
    }

    /**
     * 添加订单明细
     *
     * @param tradeItemEntity 订单明细信息
     * @return 结果
     */
    @Operation(summary = "添加订单明细", description = "添加订单明细")
    @PostMapping("/insert")
    public int insert(@RequestBody TradeItemEntity tradeItemEntity) {
        return tradeItemService.insert(tradeItemEntity);
    }
    /**
     * 修改订单明细
     *
     * @param tradeItemEntity 订单明细信息
     * @return 结果
     */
    @Operation(summary = "修改订单明细", description = "修改订单明细")
    @PostMapping("/update")
    public int update(@RequestBody TradeItemEntity tradeItemEntity) {
        return tradeItemService.update(tradeItemEntity);
    }
    /**
     * 批量删除订单明细
     *
     * @param ids 订单明细ID集合
     * @return 结果
     */
    @Operation(summary = "批量删除订单明细", description = "批量删除订单明细")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return tradeItemService.deleteByIds(ids);
    }

}
