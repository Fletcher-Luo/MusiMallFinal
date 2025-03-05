package com.ByteAndHeartDance.auth.entity.auth;

import com.ByteAndHeartDance.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Schema(name = "角色菜单关联实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleMenuEntity extends BaseEntity {
	

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
}
