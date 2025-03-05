package com.ByteAndHeartDance.auth.entity.auth;

import com.ByteAndHeartDance.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "岗位实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobEntity extends BaseEntity {


    /**
     * 岗位名称
     */
    @Schema(name = "岗位名称")
    private String name;

    /**
     * 岗位排序
     */
    @Schema(name = "岗位排序")
    private Integer sort;

    /**
     * 部门ID
     */
    @Schema(name = "部门ID")
    private Long deptId;

    /**
     * 有效状态 1:有效 0:无效
     */
    @Schema(name = "有效状态 1:有效 0:无效")
    private Boolean validStatus;
}
