package com.ByteAndHeartDance.order.entity.otherService.feign;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "购物车feign接口分页信息")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartInfoPage {
    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer totalPage;


    /**
     * 总记录数
     */
    private Integer totalCount;

    /**
     * 数据
     */
    private List<CartInfo> data;
}
