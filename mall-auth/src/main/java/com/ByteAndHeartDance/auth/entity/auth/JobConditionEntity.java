package com.ByteAndHeartDance.auth.entity.auth;

import com.ByteAndHeartDance.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(name = "岗位查询条件实体")
@Data
public class JobConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(name = "ID")
    private Long id;

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
    private Integer validStatus;

    /**
     * 创建人ID
     */
    @Schema(name = "创建人ID")
    private Long createUserId;

    /**
     * 创建人名称
     */
    @Schema(name = "创建人名称")
    private String createUserName;

    /**
     * 创建日期
     */
    @Schema(name = "创建日期")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Schema(name = "修改人ID")
    private Long updateUserId;

    /**
     * 修改人名称
     */
    @Schema(name = "修改人名称")
    private String updateUserName;

    /**
     * 修改时间
     */
    @Schema(name = "修改时间")
    private Date updateTime;

    /**
     * 是否删除 1：已删除 0：未删除
     */
    @Schema(name = "是否删除 1：已删除 0：未删除")
    private Integer isDel;
}
