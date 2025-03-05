package com.ByteAndHeartDance.auth.controller.auth;

import com.ByteAndHeartDance.auth.entity.auth.UserRoleConditionEntity;
import com.ByteAndHeartDance.auth.entity.auth.UserRoleEntity;
import com.ByteAndHeartDance.auth.service.auth.UserRoleService;
import com.ByteAndHeartDance.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Tag(name = "用户角色关联操作", description = "用户角色关联接口")
@RestController
@RequestMapping("/v1/userRole")
public class UserRoleController {
	
	private final UserRoleService userRoleService;

	public UserRoleController(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	/**
	 * 通过id查询用户角色关联信息
	 *
	 * @param id 系统ID
	 * @return 用户角色关联信息
	 */
	@Operation(summary = "通过id查询用户角色关联信息", description = "通过id查询用户角色关联信息")
	@GetMapping("/findById")
	public UserRoleEntity findById(Long id) {
		return userRoleService.findById(id);
	}

	/**
    * 根据条件查询用户角色关联列表
    *
    * @param userRoleConditionEntity 条件
    * @return 用户角色关联列表
    */
	@Operation(summary = "根据条件查询用户角色关联列表", description = "根据条件查询用户角色关联列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<UserRoleEntity> searchByPage(@RequestBody UserRoleConditionEntity userRoleConditionEntity) {
		return userRoleService.searchByPage(userRoleConditionEntity);
	}


	/**
     * 添加用户角色关联
     *
     * @param userRoleEntity 用户角色关联实体
     * @return 影响行数
     */
	@Operation(summary = "添加用户角色关联", description = "添加用户角色关联")
	@PostMapping("/insert")
	public int insert(@RequestBody UserRoleEntity userRoleEntity) {
		return userRoleService.insert(userRoleEntity);
	}

	/**
     * 修改用户角色关联
     *
     * @param userRoleEntity 用户角色关联实体
     * @return 影响行数
     */
	@Operation(summary = "修改用户角色关联", description = "修改用户角色关联")
	@PostMapping("/update")
	public int update(@RequestBody UserRoleEntity userRoleEntity) {
		return userRoleService.update(userRoleEntity);
	}

	/**
     * 删除用户角色关联
     *
     * @param id 用户角色关联ID
     * @return 影响行数
     */
	@Operation(summary = "删除用户角色关联", description = "删除用户角色关联")
	@PostMapping("/deleteById")
	public int deleteById(@RequestBody @NotNull Long id) {
		return userRoleService.deleteById(id);
	}
}
