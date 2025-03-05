package com.ByteAndHeartDance.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(name = "权限用户Vo")
@Data
public class AuthUserEntity {

    /**
     * 唯一标识
     */
    @NotBlank(message = "唯一标识不能为空")
    @Schema(name = "唯一标识")
    private String uuid;

    /**
     * 用户名称
     */
    @NotBlank(message = "用户名称不能为空")
    @Schema(name = "用户名称")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Schema(name = "密码")
    @NotBlank
    private String password;

    /**
     * 验证码
     */
//    @NotBlank(message = "验证码不能为空")
    @Schema(name = "验证码")
    private String code;

    /**
     * 手机号
     */
    @Schema(name = "手机号")
    private String phone;
}
