package com.ByteAndHeartDance.auth.entity.auth;

import com.ByteAndHeartDance.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Schema(name = "角色菜单关联查询条件实体")
@Data
public class RoleMenuConditionEntity extends RequestPageEntity {


    /**
     * ID
     */
    @Schema(name = "ID")
    private Long id;

    /**
     * 角色ID
     */
    @Schema(name = "角色ID")
    private Long roleId;

    /**
     * 菜单ID
     */
    @Schema(name = "菜单ID")
    private Long menuId;

    /**
     * 角色ID集合
     */
    private List<Long> roleIdList;
}
