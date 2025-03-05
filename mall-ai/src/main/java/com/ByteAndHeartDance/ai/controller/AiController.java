package com.ByteAndHeartDance.ai.controller;

import cn.hutool.core.util.StrUtil;
import com.ByteAndHeartDance.ai.component.ShoppingAssistant;
import com.ByteAndHeartDance.ai.dto.*;
import com.ByteAndHeartDance.ai.entity.UserWebEntity;
import com.ByteAndHeartDance.ai.exception.BusinessException;
import com.ByteAndHeartDance.ai.feignclient.OrderFeignClient;
import com.ByteAndHeartDance.ai.feignclient.ProductFeignClient;
import com.ByteAndHeartDance.ai.feignclient.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
public class AiController {

  private final ShoppingAssistant shoppingAssistant;
  private final UserFeignClient userFeignClient;

  @Autowired private ProductFeignClient productFeignClient;

  @Autowired private OrderFeignClient orderFeignClient;

  public AiController(ShoppingAssistant shoppingAssistant, UserFeignClient userFeignClient) {
    this.shoppingAssistant = shoppingAssistant;
    this.userFeignClient = userFeignClient;
  }

  /**
   * Chat with AI chatId为空时，表示创建新的对话，系统会分配一个chatId，并返回
   *
   * @param chatRequest
   * @return 返回chatId和模型输出，新对话会返回一个系统生成的chatId，后续同一个对话需要传入该chatId，旧对话会返回原chatId
   */
  @PostMapping(path = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ChatStreamResponse> chat(
      @RequestHeader("Authorization") String token, @RequestBody ChatRequest chatRequest) {
    return shoppingAssistant.chat(chatRequest.getChatId(), chatRequest.getUserMessage(), token);
  }

  /**
   * AI聊天测试借口，不实际调用AI模型
   *
   * @param chatRequest
   * @return
   */
  @PostMapping(path = "/chat/test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ChatStreamResponse> chatTest(
      @RequestHeader("Authorization") String token, @RequestBody ChatRequest chatRequest) {
    String chatId = chatRequest.getChatId();
    String userMessage = chatRequest.getUserMessage();
    UserWebEntity userDetail = null;
    try {
      userDetail = userFeignClient.getUserDetail(token);
    } catch (Exception ignored) {
      throw new BusinessException("请先登录！");
    }
    if (StrUtil.isEmpty(chatId)) {
      chatId = userDetail.getUserName() + "_" + System.currentTimeMillis();
    }
    return Flux.concat(
        Flux.just(new ChatStreamResponse.InitResponse(chatId)),
        Flux.just(new ChatStreamResponse.MessageResponse("你好！我是你的AI助手，有什么可以帮助你的吗？")),
            Flux.just(new ChatStreamResponse.MessageResponse("订单号为1894294307147247616")),
        Flux.just(new ChatStreamResponse.MessageResponse("今天天气不错，你想要了解什么？")));

  }

  /**
   * Check if the service is alive default userMessage: "Tell me a joke."
   *
   * @return
   */
  @GetMapping(path = "/hello", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> hello(@RequestHeader("Authorization") String token) {
    return shoppingAssistant.hello(token);
  }

  /**
   * 测试feignclient调用
   *
   * @return
   */
  @GetMapping("/getUserDetail")
  public UserWebEntity getUserDetail(@RequestHeader("Authorization") String token) {
    UserWebEntity userDetail = null;
    try {
      userDetail = userFeignClient.getUserDetail(token);
    } catch (Exception e) {
    }
    return userDetail;
  }

  @GetMapping("/searchProduct")
  public ResponsePage<ProductDTO> searchProduct(@RequestHeader("Authorization") String token) {
    return productFeignClient.searchProductsPage(token, "笔记本", 1, 10);
  }

  @PostMapping("/createByDirect")
  CreateTradeResponse createByAI(
      @RequestHeader("Authorization") String token,
      @RequestBody CreateTradeDirectRequest createTradeDirectDTO) {
    return orderFeignClient.createByDirect(token, createTradeDirectDTO);
  }
}
