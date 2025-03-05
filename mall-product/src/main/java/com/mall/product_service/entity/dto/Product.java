package com.mall.product_service.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;


import java.math.*;
import java.util.List;

/**
 * 商品实体类（Product）
 * 该类直接映射到数据库中的 products 表。
 */
@Entity
@Table(name = "mall_product")
@Schema(description = "商品实体")
public class Product {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "系统ID")
    private Long id;

    @Column(name = "name")
    @Schema(description = "商品名称")
    private String name;

    @Column(name = "model")
    @Schema(description = "商品型号/描述")
    private String description;

    @Column(name = "cover_url")
    @Schema(description = "商品图片URL")
    private String picture;

    @Column(name = "price")
    @Schema(description = "商品价格")
    private BigDecimal price; // 使用 BigDecimal 以适应 DECIMAL 类型

    @Column(name = "quantity")
    @Schema(description = "商品数量")
    private int quantity;

    @Column(name = "remain_quantity")
    @Schema(description = "商品剩余数量")
    private int remainQuantity;

//    @ElementCollection
//    @CollectionTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "category_id")
    @Schema(description = "商品类别列表")
    private Long categories;


    @Column(name = "product_group_id")
    @Schema(description = "商品分组ID")
    private Long productGroupId;


    // 全参构造方法
    public Product(Long id, String name, String description, String picture, BigDecimal price, Long categories, int quantity, int remainQuantity,Long productGroupId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.price = price;
        this.categories = categories;
        this.quantity = quantity;
        this.remainQuantity = remainQuantity;
        this.productGroupId = productGroupId;
    }
    public Product(Long id, String name, String description, String picture, BigDecimal price, int quantity, int remainQuantity, Long categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.price = price;
        this.quantity = quantity;
        this.remainQuantity = remainQuantity;
        this.categories = categories;
        this.productGroupId =1L;

    }

    // 无参构造方法
    public Product() {}

    // Getter 和 Setter 方法

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

    public Long getCategories() {
        return categories;
    }

    public void setCategories(Long categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", remainQuantity=" + remainQuantity +
                ", categories=" + categories +
                '}';
    }
}

