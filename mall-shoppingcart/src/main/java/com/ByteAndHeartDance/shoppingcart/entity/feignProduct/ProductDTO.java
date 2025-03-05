package com.ByteAndHeartDance.shoppingcart.entity.feignProduct;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.math3.stat.descriptive.summary.Product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品数据传输对象（DTO）
 * 用于从实体类（Product）中提取数据，并在不同层之间传递数据。
 */
@Schema(description = "商品数据传输对象")
public class ProductDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 产品ID
    @Schema(description = "系统ID")
    private Long id;

    // 产品名称（不能为空）
    @Schema(description = "产品名称")
    @NotNull(message = "Product name cannot be null")
    @Size(min = 1, max = 255, message = "Product name must be between 1 and 255 characters")
    private String name;

    // 产品描述（可选，长度限制）
    @Schema(description = "产品描述")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    // 产品图片URL（不能为空）
    @Schema(description = "产品图片URL")
    @NotNull(message = "Product picture URL cannot be null")
    private String picture;

    // 产品价格（使用 BigDecimal）
    @Schema(description = "产品价格")
    @NotNull(message = "Product price cannot be null")
    private BigDecimal price;

    // 总库存（可选）
    @Schema(description = "商品库存数量")
    @NotNull(message = "Total quantity cannot be null")
    private int quantity;

    // 剩余库存
    @Schema(description = "商品剩余数量")
    @NotNull(message = "Remain quantity cannot be null")
    private int remainQuantity;

    // 产品所属类别集合（可选）
//    @Schema(description = "商品类别列表")
//    private Long categories;



    @Schema(description = "商品分组ID")
    private Long productGroupId;


    // 无参构造方法
    public ProductDTO() {}

    // 全参构造方法
    public ProductDTO(Long id, String name, String description, String picture, BigDecimal price, int quantity, int remainQuantity, Long categories, Long productGroupId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.price = price;
        this.quantity = quantity;
        this.remainQuantity = remainQuantity;
//        this.categories = categories;
        this.productGroupId = productGroupId;
    }

    public ProductDTO(Long id, String name, String description, String picture, BigDecimal price, int quantity, int remainQuantity, Long categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.price = price;
        this.quantity = quantity;
        this.remainQuantity = remainQuantity;
//        this.categories = categories;

    }

    public Long getProductGroupId() {
        return productGroupId;
    }


    public void setProductGroupId(Long productGroupId) {
        this.productGroupId = productGroupId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getRemainQuantity() {
        return remainQuantity;
    }

    public void setRemainQuantity(int remainQuantity) {
        this.remainQuantity = remainQuantity;
    }

//    public Long getCategories() {
//        return categories;
//    }

//    public void setCategories(Long categories) {
//        this.categories = categories;
//    }

//    // 这里是转换方法
//    private Product convertToEntity(ProductDTO productDTO) {
//        return new Product(
//                productDTO.getId(),
//                productDTO.getName(),
//                productDTO.getDescription(),
//                productDTO.getPicture(),
//                productDTO.getPrice() ,
//                productDTO.getCategories(),
//                productDTO.getQuantity(),
//                productDTO.getRemainQuantity(),
//                productDTO.getProductGroupId()
//
//        );
//    }


}
