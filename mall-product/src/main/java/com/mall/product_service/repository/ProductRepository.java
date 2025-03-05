package com.mall.product_service.repository;

import com.mall.product_service.entity.dto.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 根据分类查询商品，支持分页
     *
     * @param category 分类名称
     * @param pageable 分页参数
     * @return 分页结果（Page 对象）
     */
    Page<Product> findByCategoriesContaining(String category, Pageable pageable);

    /**
     * 根据名称或描述模糊搜索商品
     *
     * @param name        商品名称（支持模糊搜索）
     * @param description 商品描述（支持模糊搜索）
     * @return 符合条件的商品列表
     */
    List<Product> findByNameContainingOrDescriptionContaining(String name, String description);

    /**
     * 根据名称或描述模糊分页搜索商品
     *
     * @param name        商品名称（支持模糊搜索）
     * @param description 商品描述（支持模糊搜索）
     * @param pageable    分页参数
     * @return 符合条件的商品列表
     */
    Page<Product> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);

// === 新增库存相关查询和更新方法 ===

    /**
     * 根据产品 ID 查询商品
     *
     * @param id 商品 ID
     * @return 商品
     */
    Product findById(long id);

    /**
     * 减少库存（更新剩余库存）
     * 确保库存足够时才更新
     *
     * @param productId 商品 ID
     * @param amount    扣减数量
     * @return 更新的行数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.remainQuantity = p.remainQuantity - :amount WHERE p.id = :productId AND p.remainQuantity >= :amount")
    int reduceStock(Long productId, Integer amount);

    /**
     * 增加库存（更新剩余库存）
     *
     * @param productId 商品 ID
     * @param amount    增加数量
     * @return 更新的行数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.remainQuantity = p.remainQuantity + :amount WHERE p.id = :productId")
    int addStock(Long productId, Integer amount);

    /**
     * 查询商品的总库存
     *
     * @param productId 商品 ID
     * @return 总库存
     */
    @Query("SELECT p.quantity FROM Product p WHERE p.id = :productId")
    Integer findTotalStock(Long productId);

    /**
     * 查询商品的剩余库存
     *
     * @param productId 商品 ID
     * @return 剩余库存
     */
    @Query("SELECT p.remainQuantity FROM Product p WHERE p.id = :productId")
    Integer findRemainStock(Long productId);






}
