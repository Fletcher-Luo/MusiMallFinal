package com.ByteAndHeartDance.auth.controller.auth;

import com.ByteAndHeartDance.annotation.NoLogin;
import com.ByteAndHeartDance.auth.entity.auth.RoleConditionEntity;
import com.ByteAndHeartDance.auth.entity.auth.RoleEntity;
import com.ByteAndHeartDance.auth.service.auth.RoleService;
import com.ByteAndHeartDance.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Tag(name = "角色操作", description = "角色接口")
@RestController
@RequestMapping("/v1/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 通过id查询角色信息
     *
     * @param id 系统ID
     * @return 角色信息
     */
    @Operation(summary = "通过id查询角色信息", description = "通过id查询角色信息")
    @GetMapping("/findById")
    public RoleEntity findById(Long id) {
        return roleService.findById(id);
    }

    /**
     * 根据条件查询角色列表
     *
     * @param roleConditionEntity 条件
     * @return 角色列表
     */
    @Operation(summary = "根据条件查询角色列表", description = "根据条件查询角色列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<RoleEntity> searchByPage(@RequestBody RoleConditionEntity roleConditionEntity) {
        return roleService.searchByPage(roleConditionEntity);
    }


    /**
     * 根据查询所有角色
     *
     * @return 角色列表
     */
	@NoLogin
    @Operation(summary = "根据查询所有角色", description = "根据查询所有角色")
    @GetMapping("/all")
    public List<RoleEntity> all() {
        return roleService.all();
    }

    /**
     * 添加角色
     *
     * @param roleEntity 角色实体
     * @return 影响行数
     */
    @Operation(summary = "添加角色", description = "添加角色")
    @PostMapping("/insert")
    public int insert(@RequestBody RoleEntity roleEntity) {
        return roleService.insert(roleEntity);
    }

    /**
     * 修改角色
     *
     * @param roleEntity 角色实体
     * @return 影响行数
     */
    @Operation(summary = "修改角色", description = "修改角色")
    @PostMapping("/update")
    public int update(@RequestBody RoleEntity roleEntity) {
        return roleService.update(roleEntity);
    }

    /**
     * 批量删除角色
     *
     * @param ids 角色ID
     * @return 影响行数
     */
    @Operation(summary = "批量删除角色", description = "批量删除角色")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return roleService.deleteByIds(ids);
    }

}
