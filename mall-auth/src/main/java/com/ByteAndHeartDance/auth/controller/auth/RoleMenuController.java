package com.ByteAndHeartDance.auth.controller.auth;

import com.ByteAndHeartDance.auth.entity.auth.RoleMenuConditionEntity;
import com.ByteAndHeartDance.auth.entity.auth.RoleMenuEntity;
import com.ByteAndHeartDance.auth.service.auth.RoleMenuService;
import com.ByteAndHeartDance.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Tag(name = "角色菜单关联操作", description = "角色菜单关联接口")
@RestController
@RequestMapping("/v1/roleMenu")
public class RoleMenuController {
	
	private RoleMenuService roleMenuService;

	public RoleMenuController(RoleMenuService roleMenuService) {
		this.roleMenuService = roleMenuService;
	}

	/**
	 * 通过id查询角色菜单关联信息
	 *
	 * @param id 系统ID
	 * @return 角色菜单关联信息
	 */
	@Operation(summary = "通过id查询角色菜单关联信息", description = "通过id查询角色菜单关联信息")
	@GetMapping("/findById")
	public RoleMenuEntity findById(Long id) {
		return roleMenuService.findById(id);
	}

	/**
    * 根据条件查询角色菜单关联列表
    *
    * @param roleMenuConditionEntity 条件
    * @return 角色菜单关联列表
    */
	@Operation(summary = "根据条件查询角色菜单关联列表", description = "根据条件查询角色菜单关联列表")
	@PostMapping("/searchByPage")
	public ResponsePageEntity<RoleMenuEntity> searchByPage(@RequestBody RoleMenuConditionEntity roleMenuConditionEntity) {
		return roleMenuService.searchByPage(roleMenuConditionEntity);
	}


	/**
     * 添加角色菜单关联
     *
     * @param roleMenuEntity 角色菜单关联实体
     * @return 影响行数
     */
	@Operation(summary = "添加角色菜单关联", description = "添加角色菜单关联")
	@PostMapping("/insert")
	public int insert(@RequestBody RoleMenuEntity roleMenuEntity) {
		return roleMenuService.insert(roleMenuEntity);
	}

	/**
     * 修改角色菜单关联
     *
     * @param roleMenuEntity 角色菜单关联实体
     * @return 影响行数
     */
	@Operation(summary = "修改角色菜单关联", description = "修改角色菜单关联")
	@PostMapping("/update")
	public int update(@RequestBody RoleMenuEntity roleMenuEntity) {
		return roleMenuService.update(roleMenuEntity);
	}

	/**
     * 删除角色菜单关联
     *
     * @param id 角色菜单关联ID
     * @return 影响行数
     */
	@Operation(summary = "删除角色菜单关联", description = "删除角色菜单关联")
	@PostMapping("/deleteById")
	public int deleteById(@RequestBody @NotNull Long id) {
		return roleMenuService.deleteById(id);
	}
}
