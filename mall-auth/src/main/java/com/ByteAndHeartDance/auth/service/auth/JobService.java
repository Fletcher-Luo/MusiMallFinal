package com.ByteAndHeartDance.auth.service.auth;

import com.ByteAndHeartDance.auth.entity.auth.JobConditionEntity;
import com.ByteAndHeartDance.auth.entity.auth.JobEntity;
import com.ByteAndHeartDance.auth.mapper.auth.JobMapper;
import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.mapper.BaseMapper;
import com.ByteAndHeartDance.service.BaseService;
import com.ByteAndHeartDance.utils.AssertUtil;
import com.ByteAndHeartDance.utils.FillUserUtil;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class JobService extends BaseService<JobEntity, JobConditionEntity> {

    private final JobMapper jobMapper;

    public JobService(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    /**
     * 查询岗位信息
     *
     * @param id 岗位ID
     * @return 岗位信息
     */
    public JobEntity findById(Long id) {
        return jobMapper.findById(id);
    }

    /**
     * 根据条件分页查询岗位列表
     *
     * @param jobConditionEntity 岗位信息
     * @return 岗位集合
     */
    public ResponsePageEntity<JobEntity> searchByPage(JobConditionEntity jobConditionEntity) {
        return super.searchByPage(jobConditionEntity);
    }

    /**
     * 新增岗位
     *
     * @param jobEntity 岗位信息
     * @return 结果
     */
    public int insert(JobEntity jobEntity) {
        return jobMapper.insert(jobEntity);
    }

    /**
     * 修改岗位
     *
     * @param jobEntity 岗位信息
     * @return 结果
     */
    public int update(JobEntity jobEntity) {
        return jobMapper.update(jobEntity);
    }

    /**
     * 删除岗位对象
     *
     * @param ids 系统ID
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<JobEntity> jobEntities = jobMapper.findByIds(ids);
        AssertUtil.notEmpty(jobEntities, "岗位已被删除");

        JobEntity jobEntity = new JobEntity();
        FillUserUtil.fillUpdateUserInfo(jobEntity);
        return jobMapper.deleteByIds(ids, jobEntity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return jobMapper;
    }
}
