package com.ByteAndHeartDance.auth.mapper.auth;

import com.ByteAndHeartDance.auth.entity.auth.RoleConditionEntity;
import com.ByteAndHeartDance.auth.entity.auth.RoleEntity;
import com.ByteAndHeartDance.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface RoleMapper extends BaseMapper<RoleEntity, RoleConditionEntity> {
    /**
     * 查询角色信息
     *
     * @param id 角色ID
     * @return 角色信息
     */
    RoleEntity findById(Long id);

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色
     */
    List<RoleEntity> findRoleByUserId(Long userId);

    /**
     * 添加角色
     *
     * @param roleEntity 角色信息
     * @return 结果
     */
    int insert(RoleEntity roleEntity);

    /**
     * 修改角色
     *
     * @param roleEntity 角色信息
     * @return 结果
     */
    int update(RoleEntity roleEntity);


    /**
     * 批量删除角色
     *
     * @param ids    角色ID
     * @param entity 角色实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") RoleEntity entity);


    /**
     * 批量查询角色信息
     *
     * @param ids 角色ID
     * @return 角色信息
     */
    List<RoleEntity> findByIds(List<Long> ids);

}
