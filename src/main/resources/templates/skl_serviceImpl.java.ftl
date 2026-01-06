package ${packageName}.service.impl;

import ${packageName}.entity.query.${ClassName}Query;
import ${packageName}.entity.vo.${ClassName}VO;
import ${packageName}.entity.bo.${ClassName}BO;
import ${packageName}.entity.${ClassName};
import ${packageName}.mapper.${ClassName}Mapper;
import ${packageName}.service.${ClassName}Service;
import ${packageName}.service.query.${ClassName}ServiceQuery;
import ${packageName}.service.results.${ClassName}ServiceResults;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Setter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.gb.utils.GeneralConvertor;

import java.util.List;


/**
 * ${functionName}，Service服务实现层
 * 代码生成器
 *
 * @author ${author}
 * @className ${ClassName}ServiceImpl
 * @time ${date}
 */
@Slf4j
@Service
@Setter(onMethod_ = {@Autowired})
public class ${ClassName}ServiceImpl extends ServiceImpl<${ClassName}Mapper, ${ClassName}> implements ${ClassName}Service {


    /**
     * ${functionName}
     */
    private ${ClassName}Mapper ${className}Mapper;


    /**
     * ${functionName}
     */
    private ${ClassName}ServiceResults ${className}ServiceResults;


    /**
     * ${functionName}增强条件
     */
    private ${ClassName}ServiceQuery ${className}ServiceQuery;


    /**
     * 集合
     *
     * @param ${className}Query ${functionName}
     * @return List<${ClassName}VO>
     * @author ${author}
     * @methodName listEnhance
     * @time ${date}
     */
    @Override
    public List<${ClassName}VO> listEnhance(${ClassName}Query ${className}Query) {
        ${ClassName} ${className} = GeneralConvertor.convertor(${className}Query, ${ClassName}.class);
        QueryWrapper<${ClassName}> queryWrapper = new QueryWrapper<>(${className});
        // 自动生成查询，禁止手动写语句
        ${className}ServiceQuery.query(${className}Query, queryWrapper);
        // 人工查询条件
        queryArtificial(${className}Query, queryWrapper);
        // DO数据
        List<${ClassName}> ${className}DO = ${className}Mapper.selectList(queryWrapper);
        // VO数据
        List<${ClassName}VO> ${className}VO = GeneralConvertor.convertor(${className}DO, ${ClassName}VO.class);
        // 判断是否增强
        if (${className}Query.getAssignment() == null) {
            return ${className}ServiceResults.assignment(${className}VO);
        } else {
            return ${className}Query.getAssignment() ? ${className}ServiceResults.assignment(${className}VO) : ${className}VO;
        }
    }


    /**
     * 分页
     *
     * @param page
     * @param ${className}Query ${functionName}
     * @return Page<${ClassName}VO>
     * @author ${author}
     * @methodName pageEnhance
     * @time ${date}
     */
    @Override
    public Page<${ClassName}VO> pageEnhance(Page page, ${ClassName}Query ${className}Query) {
        ${ClassName} ${className} = GeneralConvertor.convertor(${className}Query, ${ClassName}.class);
        QueryWrapper<${ClassName}> queryWrapper = new QueryWrapper<>(${className});
        // 自动生成查询，禁止手动写语句
        ${className}ServiceQuery.query(${className}Query, queryWrapper);
        // 人工查询条件
        queryArtificial(${className}Query, queryWrapper);
        // DO数据
        Page<${ClassName}> pageDO = ${className}Mapper.selectPage(page, queryWrapper);
        // VO数据
        Page<${ClassName}VO> pageVO = ${className}ServiceResults.toPageVO(pageDO);
        // 判断是否增强
        if (${className}Query.getAssignment() == null) {
            return ${className}ServiceResults.assignment(pageVO);
        } else {
            return ${className}Query.getAssignment() ? ${className}ServiceResults.assignment(pageVO) : pageVO;
        }
    }


    /**
     * 单条
     *
     * @param ${className}Query ${functionName}
     * @return ${ClassName}VO
     * @author ${author}
     * @methodName getOneEnhance
     * @time ${date}
     */
    @Override
    public ${ClassName}VO getOneEnhance(${ClassName}Query ${className}Query) {
        ${ClassName} ${className} = GeneralConvertor.convertor(${className}Query, ${ClassName}.class);
        QueryWrapper<${ClassName}> queryWrapper = new QueryWrapper<>(${className});
        // 自动生成查询，禁止手动写语句
        ${className}ServiceQuery.query(${className}Query, queryWrapper);
        // 人工查询条件
        queryArtificial(${className}Query, queryWrapper);
        // DO数据
        ${ClassName} ${className}DO = ${className}Mapper.selectOne(queryWrapper);
        // VO数据
        ${ClassName}VO ${className}VO = GeneralConvertor.convertor(${className}DO, ${ClassName}VO.class);
        // 判断是否增强
        if (${className}Query.getAssignment() == null) {
            return ${className}ServiceResults.assignment(${className}VO);
        } else {
            return ${className}Query.getAssignment() ? ${className}ServiceResults.assignment(${className}VO) : ${className}VO;
        }
    }


    /**
     * 总数
     *
     * @param ${className}Query ${functionName}
     * @return Integer
     * @author ${author}
     * @methodName countEnhance
     * @time ${date}
     */
    @Override
    public Long countEnhance(${ClassName}Query ${className}Query) {
        ${ClassName} ${className} = GeneralConvertor.convertor(${className}Query, ${ClassName}.class);
        QueryWrapper<${ClassName}> queryWrapper = new QueryWrapper<>(${className});
        // 自动生成查询，禁止手动写语句
        ${className}ServiceQuery.query(${className}Query, queryWrapper);
        // 人工查询条件
        queryArtificial(${className}Query, queryWrapper);
        return ${className}Mapper.selectCount(queryWrapper);
    }


    /**
     * 新增
     *
     * @param ${className}BO ${functionName}
     * @return String
     * @author ${author}
     * @methodName saveEnhance
     * @time ${date}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
    public String saveEnhance(${ClassName}BO ${className}BO) {
        ${ClassName} ${className} = GeneralConvertor.convertor(${className}BO, ${ClassName}.class);
        ${className}Mapper.insert(${className});
        return ${className}.getId();
    }


    /**
     * 修改
     *
     * @param ${className}BO ${functionName}
     * @return Boolean
     * @author ${author}
     * @methodName updateEnhance
     * @time ${date}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
    public Boolean updateEnhance(${ClassName}BO ${className}BO) {
        ${ClassName} ${className} = GeneralConvertor.convertor(${className}BO, ${ClassName}.class);
        UpdateWrapper<${ClassName}> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", ${className}BO.getId());
        Integer i = ${className}Mapper.update(${className}, updateWrapper);
        return i > 0 ? true : false;
    }


    /**
     * 删除
     *
     * @param ${className}BO ${functionName}
     * @return Boolean
     * @author ${author}
     * @methodName removeEnhance
     * @time ${date}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
    public Boolean removeEnhance(${ClassName}BO ${className}BO) {
        ${ClassName} ${className} = GeneralConvertor.convertor(${className}BO, ${ClassName}.class);
        QueryWrapper<${ClassName}> queryWrapper = new QueryWrapper<>(${className});
        Integer i = ${className}Mapper.delete(queryWrapper);
        return i > 0 ? true : false;
    }


    /**
     * 人工查询条件
     *
     * @param ${className}Query ${functionName}
     * @return QueryWrapper
     * @author ${author}
     * @methodName queryArtificial
     * @time ${date}
     */
    private QueryWrapper queryArtificial(${ClassName}Query ${className}Query, QueryWrapper<${ClassName}> queryWrapper) {
        return queryWrapper;
    }
}