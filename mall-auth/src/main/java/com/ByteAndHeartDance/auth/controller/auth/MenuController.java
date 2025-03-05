package com.ByteAndHeartDance.auth.controller.auth;

import com.ByteAndHeartDance.annotation.NoLogin;
import com.ByteAndHeartDance.auth.dto.auth.MenuTreeDTO;
import com.ByteAndHeartDance.auth.entity.auth.MenuConditionEntity;
import com.ByteAndHeartDance.auth.entity.auth.MenuEntity;
import com.ByteAndHeartDance.auth.service.auth.MenuService;
import com.ByteAndHeartDance.entity.ResponsePageEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "菜单操作", description = "菜单接口")
@RestController
@RequestMapping("/v1/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * 通过id查询菜单信息
     *
     * @param id 系统ID
     * @return 菜单信息
     */
    @Operation(summary = "通过id查询菜单信息", description = "通过id查询菜单信息")
    @GetMapping("/findById")
    public MenuEntity findById(Long id) {
        return menuService.findById(id);
    }

    /**
     * 根据条件查询菜单列表
     *
     * @param menuConditionEntity 条件
     * @return 菜单列表
     */
    @NoLogin
    @Operation(summary = "根据条件查询菜单列表", description = "根据条件查询菜单列表")
    @PostMapping("/searchByPage")
    public ResponsePageEntity<MenuEntity> searchByPage(@RequestBody MenuConditionEntity menuConditionEntity) {
        return menuService.searchByPage(menuConditionEntity);
    }

    /**
     * 获取菜单树
     *
     * @return 菜单列表
     */
    @Operation(summary = "获取菜单树", description = "获取菜单树")
    @GetMapping("/getMenuTree")
    public List<MenuTreeDTO> getMenuTree() {
        return menuService.getMenuTree();
    }


    /**
     * 获取下级菜单
     *
     * @return 菜单列表
     */
    @Operation(summary = "获取下级菜单", description = "获取下级菜单")
    @GetMapping("/getChild")
    public List<Long> getChild(@RequestParam("id") Long id) {
        return menuService.getChild(id);
    }

    /**
     * 获取逐级加载的菜单
     *
     * @return 菜单列表
     */
    @Operation(summary = "获取逐级加载的菜单", description = "获取逐级加载的菜单")
    @PostMapping("/getMenu")
    public List<MenuTreeDTO> getMenu(@RequestBody MenuConditionEntity menuConditionEntity) {
        return menuService.getMenu(menuConditionEntity);
    }

    /**
     * 添加菜单
     *
     * @param menuEntity 菜单实体
     * @return 影响行数
     */
    @NoLogin
    @Operation(summary = "添加菜单", description = "添加菜单")
    @PostMapping("/insert")
    public int insert(@RequestBody MenuEntity menuEntity) {
        return menuService.insert(menuEntity);
    }

    /**
     * 修改菜单
     *
     * @param menuEntity 菜单实体
     * @return 影响行数
     */
    @NoLogin
    @Operation(summary = "修改菜单", description = "修改菜单")
    @PostMapping("/update")
    public int update(@RequestBody MenuEntity menuEntity) {
        return menuService.update(menuEntity);
    }

    /**
     * 批量删除菜单
     *
     * @param ids 菜单ID
     * @return 影响行数
     */
    @NoLogin
    @Operation(summary = "删除菜单", description = "删除菜单")
    @PostMapping("/deleteByIds")
    public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
        return menuService.deleteByIds(ids);
    }
}
