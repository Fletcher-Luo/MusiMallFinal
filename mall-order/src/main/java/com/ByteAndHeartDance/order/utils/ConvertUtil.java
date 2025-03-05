package com.ByteAndHeartDance.order.utils;

import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.order.entity.order.tradeItem.TradeItemEntity;
import com.ByteAndHeartDance.order.entity.otherService.CartProductsDetail;
import com.ByteAndHeartDance.order.entity.otherService.CartProductsSimple;
import com.ByteAndHeartDance.order.entity.otherService.ProductInfo;
import com.ByteAndHeartDance.order.entity.otherService.feign.CartInfo;
import com.ByteAndHeartDance.order.entity.otherService.feign.CartInfoPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** 转换工具类 */
@Slf4j
@Component
public class ConvertUtil {


  public TradeItemEntity convertToTradeItemEntity(ProductInfo productInfo) {
    TradeItemEntity tradeItemEntity = new TradeItemEntity();
    tradeItemEntity.setProductId(productInfo.getId());
    tradeItemEntity.setProductName(productInfo.getName());
    tradeItemEntity.setPrice(BigDecimal.valueOf(productInfo.getPrice()));
    tradeItemEntity.setModel(productInfo.getDescription());
    tradeItemEntity.setPicture(productInfo.getPicture());
    return tradeItemEntity;
  }

  public List<CartProductsSimple> convertCartProductsList(
      List<CartProductsDetail> cartProductsDetail) {
    return cartProductsDetail.stream()
        .map(
            cartProductsDetail1 ->
                new CartProductsSimple(
                    cartProductsDetail1.getProductId(), cartProductsDetail1.getQuantity()))
        .toList();
  }

  public ResponsePageEntity<CartProductsDetail> convertToResponsePageCartProductsDetailEntity(
      CartInfoPage obj) {
    ResponsePageEntity<CartProductsDetail> responsePageEntity =
        new ResponsePageEntity<>(0, 0, 0, 0, new ArrayList<>());
    BeanUtils.copyProperties(obj, responsePageEntity);
    log.info("responsePageEntity: {},obj:{}", responsePageEntity,obj);

    return responsePageEntity;
  }

  public  List<CartProductsDetail> convertTocartProductsDetailcartProductsDetailList(List<CartInfo> data) {
    List<CartProductsDetail> cartProductsDetailList = new ArrayList<>();
    for(CartInfo cartInfo:data){
        CartProductsDetail cartProductsDetail = new CartProductsDetail();
        cartProductsDetail.setProductId(cartInfo.getProductId());
        cartProductsDetail.setProductName(cartInfo.getProductName());
        cartProductsDetail.setModel(cartInfo.getModel());
        cartProductsDetail.setPrice(cartInfo.getPrice());
        cartProductsDetail.setCover(cartInfo.getCover());
        cartProductsDetail.setQuantity(cartInfo.getQuantity());
        cartProductsDetail.setStock(cartInfo.getStock());
        cartProductsDetail.setTotalAmount(cartInfo.getPrice().multiply(BigDecimal.valueOf(cartInfo.getQuantity())));


        cartProductsDetailList.add(cartProductsDetail);
    }
    return cartProductsDetailList;
  }
}
