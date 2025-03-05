package com.ByteAndHeartDance.auth.entity.auth;

import com.ByteAndHeartDance.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "用户角色关联实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRoleEntity extends BaseEntity {


    /**
     * 用户ID
     */
    @Schema(name = "用户ID")
    private Long userId;

    /**
     * 角色ID
     */
    @Schema(name = "角色ID")
    private Long roleId;
}
