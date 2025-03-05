package com.ByteAndHeartDance.auth.mapper.auth;

import com.ByteAndHeartDance.auth.entity.auth.UserAvatarConditionEntity;
import com.ByteAndHeartDance.auth.entity.auth.UserAvatarEntity;

import java.util.List;

public interface UserAvatarMapper {
	/**
	 * 查询用户头像信息
	 *
	 * @param id 用户头像ID
	 * @return 用户头像信息
	 */
	UserAvatarEntity findById(Long id);

	/**
	 * 根据条件查询用户头像列表
	 *
	 * @param userAvatarConditionEntity 用户头像信息
	 * @return 用户头像集合
	 */
	List<UserAvatarEntity> searchByCondition(UserAvatarConditionEntity userAvatarConditionEntity);

	/**
	 * 根据条件查询用户头像数量
	 *
	 * @param userAvatarConditionEntity 用户头像信息
	 * @return 用户头像集合
	 */
	int searchCount(UserAvatarConditionEntity userAvatarConditionEntity);

	/**
	 * 添加用户头像
	 *
	 * @param userAvatarEntity 用户头像信息
	 * @return 结果
	 */
	int insert(UserAvatarEntity userAvatarEntity);

	/**
	 * 修改用户头像
	 *
	 * @param userAvatarEntity 用户头像信息
	 * @return 结果
	 */
	int update(UserAvatarEntity userAvatarEntity);

	/**
	 * 删除用户头像
	 *
	 * @param id 用户头像ID
	 * @return 结果
	 */
	int deleteById(Long id);

}