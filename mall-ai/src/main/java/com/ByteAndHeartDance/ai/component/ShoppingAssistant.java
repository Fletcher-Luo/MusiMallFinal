package com.ByteAndHeartDance.ai.component;

import cn.hutool.core.util.StrUtil;
import com.ByteAndHeartDance.ai.dto.ChatStreamResponse;
import com.ByteAndHeartDance.ai.entity.UserWebEntity;
import com.ByteAndHeartDance.ai.feignclient.UserFeignClient;
import com.ByteAndHeartDance.ai.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class ShoppingAssistant {
  private final ChatClient chatClient;
  private final UserFeignClient userFeignClient;

  public ShoppingAssistant(
      ChatClient.Builder modelBuilder, ChatMemory chatMemory, UserFeignClient userFeignClient) {
    this.userFeignClient = userFeignClient;
    this.chatClient =
        modelBuilder
            .defaultSystem(
                """
            您是Musi商城的购物助手。
            请以友好、乐于助人且愉快的方式来回复。
            您正在通过在线聊天系统与客户互动。
            您能够支持商品查询、自动下单操作，其余功能将在后续版本中添加，如果用户问的问题不支持请告知详情。
            用户会在您查询出的商品中选择商品，然后您可以帮助用户下单。
            下单时您必须知道用户需要什么商品和商品数量。
            下单时不需要地址和联系电话，用户有默认地址和联系电话。
            下单时请直接用提供的功能下单，不需要询问用户是否有默认地址，如果返回信息提示用户没有默认地址，请您提示用户去添加默认地址。
            下单时请注意不要填错商品id。
            在询问用户之前，请检查消息历史记录以获取用户想要买的商品、商品数量等信息，尽量避免重复询问给用户造成困扰。
            使用提供的功能获取查询商品、自动下单。
            如果需要，您可以调用相应函数辅助完成。
            请讲中文。
            今天的日期是 {current_date}。
            当前用户的token是 {token}。
            """)
            .defaultAdvisors(new PromptChatMemoryAdvisor(chatMemory))
            .defaultFunctions("getUserName", "searchProductByPage", "createTrade")
            .build();
  }

  /**
   * Chat with AI chatId为空时，表示创建新的对话，系统会分配一个chatId，并返回
   *
   * @param chatId
   * @param userMessage
   * @return
   */
  public Flux<ChatStreamResponse> chat(String chatId, String userMessage, String token) {
    UserWebEntity userDetail = null;
    try {
      userDetail = userFeignClient.getUserDetail(token);
    } catch (Exception ignored) {
      log.error("Failed to get user detail from user service.");
      throw new BusinessException("请先登录！");
    }
    if (StrUtil.isEmpty(chatId)) {
      chatId = userDetail.getUserName() + "_" + System.currentTimeMillis();
      log.info("New chat, assign a chatId: {}", chatId);
    }
    log.info("ChatId: {}, User Message: {}", chatId, userMessage);
    String finalChatId = chatId;
    Flux<String> chatMessage =
        this.chatClient
            .prompt()
            .system(s -> s.param("current_date", LocalDate.now().toString()).param("token", token))
            .user(userMessage)
            .advisors(
                a ->
                    a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, finalChatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
            .stream()
            .content();
    return Flux.concat(
        Flux.just(new ChatStreamResponse.InitResponse(finalChatId)),
        chatMessage.map(ChatStreamResponse.MessageResponse::new));
  }

  public Flux<String> hello(String token) {
    return this.chatClient
        .prompt()
        .system(s -> s.param("current_date", LocalDate.now().toString()).param("token", token))
        .user("Tell me a joke.")
        .stream()
        .content();
  }
}
