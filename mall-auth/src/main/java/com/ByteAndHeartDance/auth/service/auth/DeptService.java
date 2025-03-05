package com.ByteAndHeartDance.auth.service.auth;

import cn.hutool.core.util.BooleanUtil;
import com.ByteAndHeartDance.auth.dto.auth.DeptTreeDTO;
import com.ByteAndHeartDance.auth.entity.auth.DeptConditionEntity;
import com.ByteAndHeartDance.auth.entity.auth.DeptEntity;
import com.ByteAndHeartDance.auth.mapper.auth.DeptMapper;
import com.ByteAndHeartDance.entity.ResponsePageEntity;
import com.ByteAndHeartDance.mapper.BaseMapper;
import com.ByteAndHeartDance.service.BaseService;
import com.ByteAndHeartDance.utils.AssertUtil;
import com.ByteAndHeartDance.utils.BetweenTimeUtil;
import com.ByteAndHeartDance.utils.ExcelUtil;
import com.ByteAndHeartDance.utils.FillUserUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DeptService extends BaseService<DeptEntity, DeptConditionEntity> {

    private final DeptMapper deptMapper;

    public DeptService(DeptMapper deptMapper) {
        this.deptMapper = deptMapper;
    }

    /**
     * 查询部门信息
     *
     * @param id 部门ID
     * @return 部门信息
     */
    public DeptEntity findById(Long id) {
        return deptMapper.findById(id);
    }

    /**
     * 根据条件分页查询部门列表
     *
     * @param deptConditionEntity 部门信息
     * @return 部门集合
     */
    public ResponsePageEntity<DeptTreeDTO> searchByPage(DeptConditionEntity deptConditionEntity) {
        return super.searchByPage(deptConditionEntity);
    }


    /**
     * 根据条件查询部门树
     *
     * @param deptConditionEntity 部门信息
     * @return 部门树
     */
    public List<DeptTreeDTO> searchByTree(DeptConditionEntity deptConditionEntity) {
        if (Objects.isNull(deptConditionEntity.getPid())) {
            deptConditionEntity.setPid(0L);
        }
        List<DeptEntity> dataList = deptMapper.searchByCondition(deptConditionEntity);
        return buildDeptTree(dataList, deptConditionEntity.getQueryTree());
    }

    private List<DeptTreeDTO> buildDeptTree(List<DeptEntity> dataList, Boolean queryTree) {
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        List<DeptTreeDTO> deptTreeDTOList = dataList.stream().map(x -> convertToDeptTreeDTO(x)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(deptTreeDTOList)) {
            return Collections.emptyList();
        }

        for (DeptTreeDTO deptTreeDTO : deptTreeDTOList) {
            buildChildren(deptTreeDTO, queryTree);
        }

        return deptTreeDTOList;
    }

    private DeptTreeDTO convertToDeptTreeDTO(DeptEntity deptEntity) {
        DeptTreeDTO deptTreeDTO = new DeptTreeDTO();
        deptTreeDTO.setId(deptEntity.getId());
        deptTreeDTO.setName(deptEntity.getName());
        deptTreeDTO.setLabel(deptEntity.getName());
        deptTreeDTO.setPid(deptEntity.getPid());
        deptTreeDTO.setCreateTime(deptEntity.getCreateTime());
        return deptTreeDTO;
    }

    private void buildChildren(DeptTreeDTO deptTreeDTO, Boolean queryTree) {
        deptTreeDTO.setLeaf(false);
        DeptConditionEntity deptConditionEntity = new DeptConditionEntity();
        deptConditionEntity.setPid(deptTreeDTO.getId());
        deptConditionEntity.setPageSize(0);
        List<DeptEntity> deptEntities = deptMapper.searchByCondition(deptConditionEntity);
        if (CollectionUtils.isNotEmpty(deptEntities)) {
            if (BooleanUtil.isTrue(queryTree)) {
                for (DeptEntity deptEntity : deptEntities) {
                    DeptTreeDTO childDeptTreeDTO = convertToDeptTreeDTO(deptEntity);
                    deptTreeDTO.addChildren(childDeptTreeDTO);
                    buildChildren(childDeptTreeDTO, queryTree);
                }
            }
        } else {
            deptTreeDTO.setLeaf(true);
        }
        deptTreeDTO.setSubCount(CollectionUtils.isEmpty(deptEntities) ? 0 : deptEntities.size());
        deptTreeDTO.setHasChildren(!deptTreeDTO.getLeaf());
    }

    /**
     * 新增部门
     *
     * @param deptEntity 部门信息
     * @return 结果
     */
    public int insert(DeptEntity deptEntity) {
        if (Objects.isNull(deptEntity.getPid())) {
            deptEntity.setPid(0L);
        }
        FillUserUtil.fillCreateUserInfo(deptEntity);
        return deptMapper.insert(deptEntity);
    }

    /**
     * 修改部门
     *
     * @param deptEntity 部门信息
     * @return 结果
     */
    public int update(DeptEntity deptEntity) {
        AssertUtil.notNull(deptEntity.getId(), "ID不能为空");
        FillUserUtil.fillUpdateUserInfo(deptEntity);
        return deptMapper.update(deptEntity);
    }

    /**
     * 删除部门对象
     *
     * @param ids 系统ID
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<DeptEntity> deptEntities = deptMapper.findByIds(ids);
        AssertUtil.notEmpty(deptEntities, "部门已被删除");

        DeptEntity deptEntity = new DeptEntity();
        FillUserUtil.fillUpdateUserInfo(deptEntity);
        return deptMapper.deleteByIds(ids, deptEntity);
    }


    /**
     * 导出excel
     *
     * @param response            响应
     * @param deptConditionEntity 条件
     * @throws IOException 异常
     */
    public void export(HttpServletResponse response, DeptConditionEntity deptConditionEntity) throws IOException {
        BetweenTimeUtil.parseTime(deptConditionEntity);
        deptConditionEntity.setPageSize(0);
        List<DeptEntity> deptEntities = deptMapper.searchByCondition(deptConditionEntity);
        ExcelUtil.exportExcel("部门数据", DeptEntity.class, deptEntities, response);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return deptMapper;
    }
}
