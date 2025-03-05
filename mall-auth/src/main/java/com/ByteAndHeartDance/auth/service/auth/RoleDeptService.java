package com.ByteAndHeartDance.auth.service.auth;

import com.ByteAndHeartDance.auth.entity.auth.RoleDeptConditionEntity;
import com.ByteAndHeartDance.auth.entity.auth.RoleDeptEntity;
import com.ByteAndHeartDance.auth.mapper.auth.RoleDeptMapper;
import com.ByteAndHeartDance.entity.ResponsePageEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色部门关联 服务层
 */
@Service
public class RoleDeptService {

	private final RoleDeptMapper roleDeptMapper;

	public RoleDeptService(RoleDeptMapper roleDeptMapper) {
		this.roleDeptMapper = roleDeptMapper;
	}

	/**
     * 查询角色部门关联信息
     * 
     * @param id 角色部门关联ID
     * @return 角色部门关联信息
     */
	public RoleDeptEntity findById(Long id) {
	    return roleDeptMapper.findById(id);
	}
	
	/**
     * 根据条件分页查询角色部门关联列表
     * 
     * @param roleDeptConditionEntity 角色部门关联信息
     * @return 角色部门关联集合
     */
	public ResponsePageEntity<RoleDeptEntity> searchByPage(RoleDeptConditionEntity roleDeptConditionEntity) {
		int count = roleDeptMapper.searchCount(roleDeptConditionEntity);
		if (count == 0) {
			return ResponsePageEntity.buildEmpty(roleDeptConditionEntity);
		}
		List<RoleDeptEntity> dataList = roleDeptMapper.searchByCondition(roleDeptConditionEntity);
		return ResponsePageEntity.build(roleDeptConditionEntity, count, dataList);
	}
	
    /**
     * 新增角色部门关联
     * 
     * @param roleDeptEntity 角色部门关联信息
     * @return 结果
     */
	public int insert(RoleDeptEntity roleDeptEntity) {
	    return roleDeptMapper.insert(roleDeptEntity);
	}
	
	/**
     * 修改角色部门关联
     * 
     * @param roleDeptEntity 角色部门关联信息
     * @return 结果
     */
	public int update(RoleDeptEntity roleDeptEntity) {
	    return roleDeptMapper.update(roleDeptEntity);
	}

	/**
     * 删除角色部门关联对象
     * 
     * @param id 系统ID
     * @return 结果
     */
	public int deleteById(Long id) {
		return roleDeptMapper.deleteById(id);
	}
	
}
