package com.ByteAndHeartDance.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
/**
 * ES 公共实体
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EsBaseEntity implements Serializable {

    /**
     * ID
     */
    private String id;

    /**
     * 数据
     */
    private Map<String, Object> data;
}
