package com.ByteAndHeartDance.auth.entity.auth;

import com.ByteAndHeartDance.entity.RequestConditionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Schema(name = "部门查询条件实体")
@Data
public class DeptConditionEntity extends RequestConditionEntity {


    /**
     * ID
     */
    @Schema(name = "ID")
    private Long id;

    /**
     * ids
     */
    @Schema(name = "ids")
    private List<Long> idList;

    /**
     * 名称
     */
    @Schema(name = "名称")
    private String name;

    /**
     * 上级部门
     */
    @Schema(name = "上级部门")
    private Long pid;

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

    /**
     * 是否查询树
     */
    @Schema(name = "是否查询树")
    private Boolean queryTree;
}
