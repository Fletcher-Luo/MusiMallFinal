package com.ByteAndHeartDance.helper;


import com.ByteAndHeartDance.utils.SnowFlakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统一封装ID生成服务
 */
@Component
public class IdGenerateHelper {

    @Autowired
    private SnowFlakeIdWorker snowFlakeIdWorker;

    /**
     * 生成分布式ID
     *
     * @return 分布式ID
     */
    public Long nextId() {
        return snowFlakeIdWorker.nextId();
    }
}
