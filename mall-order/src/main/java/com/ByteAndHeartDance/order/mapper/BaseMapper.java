package com.ByteAndHeartDance.order.mapper;


import java.util.List;
import org.springframework.dao.DataAccessException;

/**
 * 公共mapper
 */
public interface BaseMapper<K, V> {

    /**
     * 根据条件查询数据的数量
     *
     * @param v 实体类
     * @return 数量
     */
    int searchCount(V v);

    /**
     * 根据条件查询数据
     *
     * @param v 实体类
     * @return List<K> 实体类的集合
     * @throws DataAccessException 数据访问异常
     */
    List<K> searchByCondition(V v) throws DataAccessException;

}
