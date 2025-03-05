package com.ByteAndHeartDance.coin.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName coin_log
 */
@TableName(value ="coin_log")
@Data
public class CoinLog implements Serializable {
    /**
     * 主键id
     */
    @TableId
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 交易金额
     */
    private String amount;

    /**
     * 交易类型（0充值、1消费）
     */
    private String operatorType;

    /**
     * 交易状态（0成功、1失败、2关闭）
     */
    private String status;

    /**
     * 交易时间
     */
    private Date operatorTime;

    /**
     * 描述
     */
    private String description;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}