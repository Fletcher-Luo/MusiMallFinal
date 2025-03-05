package com.ByteAndHeartDance.ai.configuration;

import com.ByteAndHeartDance.ai.dto.*;
import com.ByteAndHeartDance.ai.dto.ai.AiCreateTradeDirectRequest;
import com.ByteAndHeartDance.ai.dto.ai.AiGetProductPageRequest;
import com.ByteAndHeartDance.ai.dto.ai.AiGetProductPageResponse;
import com.ByteAndHeartDance.ai.dto.ai.AiGetUserNameResponse;
import com.ByteAndHeartDance.ai.entity.UserWebEntity;
import com.ByteAndHeartDance.ai.feignclient.OrderFeignClient;
import com.ByteAndHeartDance.ai.feignclient.ProductFeignClient;
import com.ByteAndHeartDance.ai.feignclient.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Slf4j
@Configuration
public class AiConfiguration {

  private final UserFeignClient userFeignClient;
  private final ProductFeignClient productFeignClient;
  private final OrderFeignClient orderFeignClient;

  public AiConfiguration(
      UserFeignClient userFeignClient,
      ProductFeignClient productFeignClient,
      OrderFeignClient orderFeignClient) {
    this.userFeignClient = userFeignClient;
    this.productFeignClient = productFeignClient;
    this.orderFeignClient = orderFeignClient;
  }

  @Bean
  public ChatMemory chatMemory() {
    return new InMemoryChatMemory();
  }

  @Bean
  public VectorStore vectorStore(EmbeddingModel embeddingModel) {
    return SimpleVectorStore.builder(embeddingModel).build();
  }

  @Bean
  @Description("获取用户名，需要传入用户的token，系统会通过请求返回用户名")
  public Function<String, AiGetUserNameResponse> getUserName() {
    return (token) -> {
      log.info("大模型调用获取用户名函数");
      AiGetUserNameResponse aiGetUserNameResponse = new AiGetUserNameResponse();
      try {
        UserWebEntity userDetail = userFeignClient.getUserDetail(token);
        aiGetUserNameResponse.setUserName(userDetail.getUserName());
        aiGetUserNameResponse.setInfo("获取用户名成功");
      } catch (Exception e) {
        log.error("获取用户名失败: {}", e.getMessage());
        aiGetUserNameResponse.setUserName("未知用户");
        aiGetUserNameResponse.setInfo("获取用户名失败");
      }
      return aiGetUserNameResponse;
    };
  }

  @Bean
  @Description(
      """
          根据关键词分页查询商品，传入的参数包括商品关键词、页数和用户token。\
          第一次查询请设page = 1，如果用户对同一个关键词要求查询更多的商品，请逐步设page = 2、3、4、...。\
          这是一个请求示例的Json：{
              "query": "笔记本",
              "page": 1,
              "token": "Basic@eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc0MDQ2Mzc2N30.ciaOjyw3H0AoJBjkmv9aMc5HmFbd5UOf6ett8sVsIhhbpNLya3TVesJMSI8_B1pFdyrDGg7qCJkZbspl42jZBg",
          }\
          返回信息会包含是否调用成功和一个商品列表，每个商品包括商品id、商品名称、商品描述、图片、价格等信息。\
          用户下单时需要用到这里返回的商品id。\
          如果失败，请告知用户联系客服。\
          """)
  public Function<AiGetProductPageRequest, AiGetProductPageResponse> searchProductByPage() {
    return request -> {
      log.info("大模型调用根据关键词分页查询商品函数：{}", request);
      AiGetProductPageResponse aiGetProductPageResponse = new AiGetProductPageResponse();
      try {
        ResponsePage<ProductDTO> productDTOResponsePage =
            productFeignClient.searchProductsPage(
                request.getToken(), request.getQuery(), request.getPage(), 10);
        aiGetProductPageResponse.setResponsePage(productDTOResponsePage);
        aiGetProductPageResponse.setInfo("查询商品成功");
      } catch (Exception e) {
        log.error("查询商品失败: {}", e.getMessage());
        aiGetProductPageResponse.setInfo("查询商品失败");
      }
      return aiGetProductPageResponse;
    };
  }

  @Bean
  @Description(
      """
          给用户下单的函数，商品id、商品数量和用户token是必须的，其他信息是可选的，可以没有地址，\
          如果没有地址，系统会使用用户的默认地址，\
          如果没有默认地址，返回信息会提示用户没有默认地址，请你提示用户添加默认地址。\
          这是一个请求示例的Json：{
              "token": "Basic@eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc0MDQ2Mzc2N30.ciaOjyw3H0AoJBjkmv9aMc5HmFbd5UOf6ett8sVsIhhbpNLya3TVesJMSI8_B1pFdyrDGg7qCJkZbspl42jZBg",
              "createTradeDirectRequest": {
                  "cartProducts" : [
                      {
                          "productId" : 1832698091467509760,
                          "quantity": 1
                      }
                  ]
              }
          }\
          如果下单成功，返回的信息会包括订单号traceNo，请你在给用户的对话中包含"订单号为{traceNo}"这句话。\
          如果失败，请告知用户联系客服。\
          """)
  public Function<AiCreateTradeDirectRequest, CreateTradeResponse> createTrade() {
    return request -> {
      log.info("大模型调用下单函数：{}", request);
      CreateTradeResponse response = null;
      try {
        response =
            orderFeignClient.createByDirect(
                request.getToken(), request.getCreateTradeDirectRequest());
      } catch (Exception e) {
        log.error("下单失败: {}", e.getMessage());
        response = new CreateTradeResponse();
        response.setSubject("下单失败");
      }
      return response;
    };
  }
}
