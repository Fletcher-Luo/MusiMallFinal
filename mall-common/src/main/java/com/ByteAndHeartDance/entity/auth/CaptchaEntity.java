package com.ByteAndHeartDance.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CaptchaEntity {
    /**
     * 唯一标识
     */
    private String uuid;

    /**
     * 验证码图片
     */
    private String img;
}
