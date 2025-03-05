package com.ByteAndHeartDance.auth.controller.web;

import com.ByteAndHeartDance.annotation.NoLogin;
import com.ByteAndHeartDance.auth.service.auth.UserService;
import com.ByteAndHeartDance.entity.auth.AuthUserEntity;
import com.ByteAndHeartDance.entity.auth.CaptchaEntity;
import com.ByteAndHeartDance.entity.auth.JwtUserEntity;
import com.ByteAndHeartDance.entity.auth.UserWebEntity;
import jakarta.servlet.http.HttpServletRequest;
import com.ByteAndHeartDance.entity.auth.TokenEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 给前端提供的用户接口
 * @DateTime: 2025/1/21 16:18
 **/
@Tag(name = "web用户操作", description = "web用户接口")
@RestController
@RequestMapping("/v1/web/user")
@Validated
public class WebUserController {

  private final UserService userService;

  public WebUserController(UserService userService) {
    this.userService = userService;
  }

  @NoLogin
  @Operation(summary = "用户登录", description = "用户登录")
  @PostMapping("/login")
  public TokenEntity login(@Valid @RequestBody AuthUserEntity authUserVo) {
    return userService.login(authUserVo);
  }

  @NoLogin
  @Operation(summary = "获取验证码", description = "获取验证码")
  @GetMapping(value = "/code")
  public CaptchaEntity getCode() {
    return userService.getCode();
  }
  /**
   *用户退出登录
   */
  @NoLogin
  @Operation(summary = "用户退出登录", description = "用户退出登录")
  @PostMapping("/logout")
  public void logout(HttpServletRequest request){
    userService.logout(request);
  }

  /**
   * 获取当前登录的用户详情
   *
   * @return 用户详情
   */
  @Operation(summary = "获取当前登录的用户详情", description = "获取当前登录的用户详情")
  @GetMapping(value = "/getUserDetail")
  public UserWebEntity getUserDetail() {
    return userService.getUserDetail();
  }

  /**
   * 获取用户信息
   *
   * @return 用户信息
   */
  @NoLogin
  @Operation(summary = "获取用户信息", description = "获取用户信息")
  @GetMapping(value = "/info")
  public JwtUserEntity getUserInfo() {
    return userService.getUserInfo();
  }
}
