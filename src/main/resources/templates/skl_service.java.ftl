package ${packageName}.service;

import ${packageName}.entity.query.${ClassName}Query;
import ${packageName}.entity.vo.${ClassName}VO;
import ${packageName}.entity.bo.${ClassName}BO;
import ${packageName}.entity.${ClassName};
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;


/**
 * ${functionName}，Service服务接口层
 * 代码生成器
 *
 * @author ${author}
 * @className ${ClassName}Service
 * @time ${date}
 */
public interface ${ClassName}Service extends IService<${ClassName}> {


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
    Page<${ClassName}VO> pageEnhance(Page page, ${ClassName}Query ${className}Query);


    /**
     * 集合
     *
     * @param ${className}Query ${functionName}
     * @return List<${ClassName}VO>
     * @author ${author}
     * @methodName listEnhance
     * @time ${date}
     */
    List<${ClassName}VO> listEnhance(${ClassName}Query ${className}Query);


    /**
     * 单条
     *
     * @param ${className}Query ${functionName}
     * @return ${ClassName}VO
     * @author ${author}
     * @methodName getOneEnhance
     * @time ${date}
     */
    ${ClassName}VO getOneEnhance(${ClassName}Query ${className}Query);


    /**
     * 总数
     *
     * @param ${className}Query ${functionName}
     * @return Long
     * @author ${author}
     * @methodName countEnhance
     * @time ${date}
     */
    Long countEnhance(${ClassName}Query ${className}Query);


    /**
     * 新增
     *
     * @param ${className}BO ${functionName}
     * @return String
     * @author ${author}
     * @methodName saveEnhance
     * @time ${date}
     */
    String saveEnhance(${ClassName}BO ${className}BO);


    /**
     * 修改
     *
     * @param ${className}BO ${functionName}
     * @return Boolean
     * @author ${author}
     * @methodName updateEnhance
     * @time ${date}
     */
    Boolean updateEnhance(${ClassName}BO ${className}BO);


    /**
     * 删除
     *
     * @param ${className}BO ${functionName}
     * @return Boolean
     * @author ${author}
     * @methodName removeEnhance
     * @time ${date}
     */
    Boolean removeEnhance(${ClassName}BO ${className}BO);
}
