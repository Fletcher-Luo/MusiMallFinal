package com.ByteAndHeartDance.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserWebEntity {
  private Long id;
  private String avatarUrl;
  private String email;
  private String userName;
  private String nickName;
  private Integer sex;
}
