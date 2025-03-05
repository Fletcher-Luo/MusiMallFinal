package com.ByteAndHeartDance.auth.mapper.auth;

import com.ByteAndHeartDance.auth.entity.auth.MenuConditionEntity;
import com.ByteAndHeartDance.auth.entity.auth.MenuEntity;
import com.ByteAndHeartDance.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;


public interface MenuMapper extends BaseMapper<MenuEntity, MenuConditionEntity> {
    /**
     * 查询菜单信息
     *
     * @param id 菜单ID
     * @return 菜单信息
     */
    MenuEntity findById(Long id);

    /**
     * 查询菜单信息
     *
     * @param ids 菜单ID
     * @return 菜单信息
     */
    List<MenuEntity> findByIds(List<Long> ids);

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleIdList 角色ID集合
     * @return 菜单列表
     */
    List<MenuEntity> findMenuByRoleIdList(@Param("roleIdList") Collection<Long> roleIdList);


    /**
     * 添加菜单
     *
     * @param menuEntity 菜单信息
     * @return 结果
     */
    int insert(MenuEntity menuEntity);

    /**
     * 修改菜单
     *
     * @param menuEntity 菜单信息
     * @return 结果
     */
    int update(MenuEntity menuEntity);

    /**
     * 删除菜单
     *
     * @param ids        菜单ID
     * @param menuEntity 菜单实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("menuEntity") MenuEntity menuEntity);

}
