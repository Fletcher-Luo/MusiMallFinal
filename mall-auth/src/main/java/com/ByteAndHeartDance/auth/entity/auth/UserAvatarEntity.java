package com.ByteAndHeartDance.auth.entity.auth;

import com.ByteAndHeartDance.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "用户头像实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAvatarEntity extends BaseEntity {
	

	/**
	 * 文件名
	 */
	@Schema(name = "文件名")
	private String fileName;

	/**
	 * 路径
	 */
	@Schema(name = "路径")
	private String path;

	/**
	 * 大小
	 */
	@Schema(name = "大小")
	private String fileSize;
}
