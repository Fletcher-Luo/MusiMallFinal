package com.ByteAndHeartDance.auth.entity.auth;

import com.ByteAndHeartDance.entity.RequestPageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(name = "角色部门关联查询条件实体")
@Data
public class RoleDeptConditionEntity extends RequestPageEntity {
	

	/**
	 *  ID
     */
	@Schema(name = "ID")
	private Long id;

	/**
	 *  
     */
	@Schema(name = "")
	private Long roleId;

	/**
	 *  
     */
	@Schema(name = "")
	private Long deptId;
}
