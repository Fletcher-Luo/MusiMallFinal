package com.ByteAndHeartDance.auth.controller.auth;

import com.ByteAndHeartDance.annotation.NoLogin;
import com.ByteAndHeartDance.auth.service.auth.UserService;
import com.ByteAndHeartDance.auth.entity.auth.UserConditionEntity;
import com.ByteAndHeartDance.auth.entity.auth.UserEntity;
import com.ByteAndHeartDance.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "用户操作", description = "用户接口")
@RestController
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 通过id查询用户信息
     *
     * @param id 系统ID
     * @return 用户信息
     */
//    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "通过id查询用户信息", description = "通过id查询用户信息")
    @GetMapping("/findById")
    public UserEntity findById(Long id) {
        UserEntity userEntity = userService.findById(id);
        log.info("userEntity:{}", userEntity);
        return userEntity;
    }

    /**
     * 根据条件查询用户列表
     *
     * @param userConditionEntity 条件
     * @return 用户列表
     */
    @Operation(summary = "根据条件查询用户列表", description = "根据条件查询用户列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<UserEntity> searchByPage(@RequestBody UserConditionEntity userConditionEntity) {
        return userService.searchByPage(userConditionEntity);
    }


    /**
     * 添加用户
     *
     * @param userEntity 用户实体
     * @return 影响行数
     */
    @Operation(summary = "添加用户", description = "添加用户")
    @PostMapping("/insert")
    @NoLogin  // 不需要登录测试用
    public void insert(@RequestBody UserEntity userEntity) {
        userService.insert(userEntity);
    }

    /**
     * 修改用户
     *
     * @param userEntity 用户实体
     * @return 影响行数
     */
    @Operation(summary = "修改用户", description = "修改用户")
    @PostMapping("/update")
    public int update(@RequestBody UserEntity userEntity) {
        return userService.update(userEntity);
    }


    /**
     * 重置密码
     *
     * @param ids 用户ID
     * @return 影响行数
     */
    @Operation(summary = "重置密码", description = "重置密码")
    @PostMapping("/resetPwd")
    @NoLogin
    public int resetPwd(@RequestBody @NotNull List<Long> ids) {
        return userService.resetPwd(ids);
    }
}
