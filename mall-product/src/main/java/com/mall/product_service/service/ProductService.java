package com.mall.product_service.service;

import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.helper.IdGenerateHelper;
import com.mall.product_service.entity.dto.Product;
import com.mall.product_service.entity.dto.ProductDTO;
import com.mall.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;


import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/** 商品服务类，包含商品的增删改查操作 */
@Service
public class ProductService {

  private final ProductRepository productRepository;

//  @Autowired
//  private IdGenerateHelper idGenerateHelper;


  // 使用构造方法注入 ProductRepository
  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  /**
   * 获取商品列表（支持分页和按类别筛选），返回 DTO 列表
   *
   * @param page 页码（从 1 开始）
   * @param pageSize 每页商品数量
   * @param categoryName 商品类别名称（可选）
   * @return 商品 DTO 列表
   */
  public ResponsePageEntity<ProductDTO> getProducts(int page, int pageSize, String categoryName) {
    // 分页请求对象
    Page<Product> pageResponse;
    Pageable pageable;
    // 如果 page 为 -1，则不分页
    if (page == -1) {
      pageable = Pageable.unpaged();
    } else {
      pageable = PageRequest.of(page - 1, pageSize);
    }
    // 按类别筛选和分页查询
    if (categoryName == null || categoryName.isEmpty()) {
      pageResponse = productRepository.findAll(pageable);
    } else {
      pageResponse = productRepository.findByCategoriesContaining(categoryName, pageable);
    }
    // 计算总页数
    int totalPages = pageResponse.getTotalPages();
    int totalCnt = (int) pageResponse.getTotalElements();

    ResponsePageEntity<ProductDTO> responsePageEntity =
        new ResponsePageEntity<>(
            page,
            pageSize,
            totalPages,
            totalCnt,
            pageResponse.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    return responsePageEntity;
  }

  /**
   * 根据商品 ID 获取单个商品信息
   *
   * @param id 商品 ID
   * @return 商品 DTO，如果商品不存在则返回 null
   */
  public ProductDTO getProductById(Long id) {
    return productRepository.findById(id).map(this::convertToDTO).orElse(null);
  }

  /**
   * 根据关键字搜索商品
   *
   * @param query 搜索关键字（对名称或描述进行模糊匹配）
   * @return 符合搜索条件的商品 DTO 列表
   */
  public List<ProductDTO> searchProducts(String query) {
    // 模糊搜索商品
    List<Product> products =
        productRepository.findByNameContainingOrDescriptionContaining(query, query);
    return products.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  /**
   * 分页搜索商品
   *
   * @param query 搜索关键字
   * @param page 页码，默认从 1 开始
   * @param pageSize 每页显示的数量，默认 10
   * @return 分页搜索结果
   */
  public ResponsePageEntity<ProductDTO> searchProductsPage(String query, int page, int pageSize) {
    Page<Product> pageResponse;
    Pageable pageable;
    if (page == -1) {
      pageable = Pageable.unpaged();
    } else {
      pageable = PageRequest.of(page - 1, pageSize);
    }
    pageResponse =
        productRepository.findByNameContainingOrDescriptionContaining(query, query, pageable);
    int totalPages = pageResponse.getTotalPages();
    int totalCnt = (int) pageResponse.getTotalElements();
    ResponsePageEntity<ProductDTO> responsePageEntity =
        new ResponsePageEntity<>(
            page,
            pageSize,
            totalPages,
            totalCnt,
            pageResponse.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    return responsePageEntity;
  }

  /**
   * 创建商品
   *
   * @param productDTO 商品 DTO（从前端传入的商品信息）
   * @return 创建成功后的商品 DTO
   */
  @Transactional
  public ProductDTO createProduct(ProductDTO productDTO) {
    // 将 DTO 转换为实体并保存
    Product product = convertToEntity(productDTO);

//    product.setId(idGenerateHelper.nextId());
    Product savedProduct = productRepository.save(product);
    return convertToDTO(savedProduct);
  }

  /**
   * 修改商品信息
   *
   * @param id 商品 ID
   * @param productDTO 修改后的商品 DTO 数据
   * @return 修改成功后的商品 DTO，如果商品不存在则返回 null
   */
  public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
    // 检查商品是否存在
    return productRepository
        .findById(id)
        .map(
            existingProduct -> {
              // 更新属性
              existingProduct.setName(productDTO.getName());
              existingProduct.setDescription(productDTO.getDescription());
              existingProduct.setPicture(productDTO.getPicture());
              existingProduct.setPrice(productDTO.getPrice());
              existingProduct.setCategories(productDTO.getCategories());
              // 保存修改后的商品
              return productRepository.save(existingProduct);
            })
        .map(this::convertToDTO)
        .orElse(null); // 如果商品不存在，返回 null
  }

  /**
   * 删除商品
   *
   * @param id 商品 ID
   */
  public void deleteProduct(Long id) {
    productRepository.deleteById(id);
  }

  // === 库存管理 ===

  /**
   * 扣减库存
   *
   * @param productId 商品ID
   * @param amount 扣减数量
   * @return 是否成功
   */
  @Transactional
  public boolean reduceStock(Long productId, Integer amount) {
    // 使用 Optional 的方式来获取 Product 实体
    Product product = productRepository.findById(productId).orElse(null);

    if (product != null && product.getRemainQuantity() >= amount) {
      // 执行扣减库存操作
      int rowsAffected = productRepository.reduceStock(productId, amount);
      return rowsAffected > 0; // 如果更新行数大于 0，则操作成功
    }
    return false; // 库存不足或商品不存在
  }

  /**
   * 增加库存
   *
   * @param productId 商品ID
   * @param amount 增加数量
   * @return 是否成功
   */
  @Transactional
  public boolean addStock(Long productId, Integer amount) {
    int rowsAffected = productRepository.addStock(productId, amount);
    return rowsAffected > 0; // 增加库存成功
  }

  /**
   * 获取商品剩余库存
   *
   * @param productId 商品ID
   * @return 剩余库存
   */
  public Integer getRemainStock(Long productId) {
    return productRepository.findRemainStock(productId);
  }

  // === DTO 和 Entity 转换方法 ===

  private ProductDTO convertToDTO(Product product) {
    return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPicture(),
            product.getPrice(), // 转换 float 为 BigDecimal
            product.getQuantity(),
            product.getRemainQuantity(),
            product.getCategories()

    );
  }

  /**
   * 将 ProductDTO 对象转换为 Product 实体对象
   *
   * @param productDTO 商品 DTO 对象
   * @return 商品实体对象
   */
  private Product convertToEntity(ProductDTO productDTO) {
    return new Product(
            productDTO.getId(),
            productDTO.getName(),
            productDTO.getDescription(),
            productDTO.getPicture(),
            productDTO.getPrice(), // 转换 BigDecimal 为 float
            productDTO.getCategories(),
            productDTO.getQuantity(),
            productDTO.getRemainQuantity(),
            productDTO.getProductGroupId()
    );
  }

}
