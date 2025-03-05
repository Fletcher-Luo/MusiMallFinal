package com.ByteAndHeartDance.auth.service.auth;

import com.ByteAndHeartDance.auth.entity.auth.RoleConditionEntity;
import com.ByteAndHeartDance.auth.entity.auth.RoleEntity;
import com.ByteAndHeartDance.auth.entity.auth.RoleMenuEntity;
import com.ByteAndHeartDance.auth.mapper.auth.RoleMapper;
import com.ByteAndHeartDance.auth.mapper.auth.RoleMenuMapper;
import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.helper.IdGenerateHelper;
import com.ByteAndHeartDance.mapper.BaseMapper;
import com.ByteAndHeartDance.service.BaseService;
import com.ByteAndHeartDance.utils.AssertUtil;
import com.ByteAndHeartDance.utils.FillUserUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class RoleService extends BaseService<RoleEntity, RoleConditionEntity> {

    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final IdGenerateHelper idGenerateHelper;

    public RoleService(RoleMapper roleMapper, RoleMenuMapper roleMenuMapper, IdGenerateHelper idGenerateHelper) {
        this.roleMapper = roleMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.idGenerateHelper = idGenerateHelper;
    }

    /**
     * 查询角色信息
     *
     * @param id 角色ID
     * @return 角色信息
     */
    public RoleEntity findById(Long id) {
        return roleMapper.findById(id);
    }

    /**
     * 根据条件分页查询角色列表
     *
     * @param roleConditionEntity 角色信息
     * @return 角色集合
     */
    public ResponsePageEntity<RoleEntity> searchByPage(RoleConditionEntity roleConditionEntity) {
        return super.searchByPage(roleConditionEntity);
    }

    /**
     * 根据查询所有角色
     *
     * @return 所有角色
     */
    public List<RoleEntity> all() {
        RoleConditionEntity roleConditionEntity = new RoleConditionEntity();
        roleConditionEntity.setPageSize(0);
        return roleMapper.searchByCondition(roleConditionEntity);
    }

    /**
     * 新增角色
     *
     * @param roleEntity 角色信息
     * @return 结果
     */
    public int insert(RoleEntity roleEntity) {
        return roleMapper.insert(roleEntity);
    }

    /**
     * 修改角色
     *
     * @param roleEntity 角色信息
     * @return 结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public int update(RoleEntity roleEntity) {
        roleMenuMapper.deleteByRoleIds(Lists.newArrayList(roleEntity.getId()));
        saveRoleMenu(roleEntity);
        return roleMapper.update(roleEntity);
    }

    private void saveRoleMenu(RoleEntity roleEntity) {
        if (CollectionUtils.isEmpty(roleEntity.getMenus())) {
            return;
        }

        List<RoleMenuEntity> roleMenuEntities = roleEntity.getMenus().stream().map(x -> {
            RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
            roleMenuEntity.setId(idGenerateHelper.nextId());
            roleMenuEntity.setRoleId(roleEntity.getId());
            roleMenuEntity.setMenuId(x.getId());
            return roleMenuEntity;
        }).collect(Collectors.toList());

        roleMenuMapper.batchInsert(roleMenuEntities);
    }

    /**
     * 批量删除角色对象
     *
     * @param ids 系统ID
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<RoleEntity> roleEntities = roleMapper.findByIds(ids);
        AssertUtil.notEmpty(roleEntities, "角色已被删除");

        RoleEntity roleEntity = new RoleEntity();
        FillUserUtil.fillUpdateUserInfo(roleEntity);
        return roleMapper.deleteByIds(ids, roleEntity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return roleMapper;
    }
}
