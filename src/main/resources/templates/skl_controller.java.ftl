package ${packageName}.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import com.gb.utils.annotations.RequestRequired;
import com.gb.utils.annotations.Methods;
import com.gb.utils.Json;
import com.gb.utils.RedisUtils;
import com.gb.utils.annotations.PreventRepeat;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.gb.utils.enumeration.ReturnCode;
import ${packageName}.service.${ClassName}Service;
import ${packageName}.entity.query.${ClassName}Query;
import ${packageName}.entity.vo.${ClassName}VO;
import ${packageName}.entity.bo.${ClassName}BO;


/**
 * ${functionName}，Comment请求层
 * 代码生成器
 *
 * @author ${author}
 * @className ${ClassName}Controller
 * @time ${date}
 */
@Slf4j
@RequestRequired
@ApiSupport(author = "${author}")
@Setter(onMethod_ = {@Autowired})
@RestController
@RequestMapping("/${tableName?replace("_","-")}")
@Api(tags = "${functionName}")
public class ${ClassName}Controller {


    /**
     * ${functionName}
     */
    private ${ClassName}Service ${className}Service;
    /**
     * redis操作
     */
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 分页
     *
     * @param ${className}Query ${functionName}
     * @return Json<Page<${ClassName}VO>>
     * @author ${author}
     * @methodName select
     * @time ${date}
     */
    @Methods(methodsName = "${functionName}集合分页查询", methods = "select")
    @ApiOperation(value = "${functionName}集合分页查询", httpMethod = "POST", notes = "${functionName}集合查询", response = Json.class)
    @PostMapping("/select")
    public Json<Page<${ClassName}VO>> select(@Validated(value = ${ClassName}Query.Select.class) @RequestBody ${ClassName}Query ${className}Query) {
        //返回内容
        return new Json(ReturnCode.成功, ${className}Service.pageEnhance(new Page(${className}Query.getCurrent(), ${className}Query.getSize()), ${className}Query));
    }


    /**
     * 集合
     *
     * @param ${className}Query ${functionName}
     * @return Json<List<${ClassName}VO>>
     * @author ${author}
     * @methodName selectList
     * @time ${date}
     */
    @Methods(methodsName = "${functionName}集合查询", methods = "selectList")
    @ApiOperation(value = "${functionName}集合查询", httpMethod = "POST", notes = "${functionName}集合查询", response = Json.class)
    @PostMapping("/selectList")
    public Json<List<${ClassName}VO>> selectList(@Validated(value = ${ClassName}Query.SelectList.class) @RequestBody ${ClassName}Query ${className}Query) {
        //返回内容
        return new Json(ReturnCode.成功, ${className}Service.listEnhance(${className}Query));
    }


    /**
     * 单条
     *
     * @param ${className}Query ${functionName}
     * @return Json<${ClassName}VO>
     * @author ${author}
     * @methodName selectOne
     * @time ${date}
     */
    @Methods(methodsName = "${functionName}单条查询", methods = "selectOne")
    @ApiOperation(value = "${functionName}单条查询", httpMethod = "POST", notes = "${functionName}单条查询", response = Json.class)
    @PostMapping("/selectOne")
    public Json<${ClassName}VO> selectOne(@Validated(value = ${ClassName}Query.SelectOne.class) @RequestBody ${ClassName}Query ${className}Query) {
        //返回内容
        return new Json(ReturnCode.成功, ${className}Service.getOneEnhance(${className}Query));
    }


    /**
     * 总数
     *
     * @param ${className}Query ${functionName}
     * @return Json<Integer>
     * @author ${author}
     * @methodName count
     * @time ${date}
     */
    @Methods(methodsName = "${functionName}总数查询", methods = "count")
    @ApiOperation(value = "${functionName}总数查询", httpMethod = "POST", notes = "${functionName}总数查询", response = Json.class)
    @PostMapping(value = "/count")
    public Json<Integer> count(@Validated(value = ${ClassName}Query.Count.class) @RequestBody ${ClassName}Query ${className}Query) {
        //返回内容
        return new Json(ReturnCode.成功, ${className}Service.countEnhance(${className}Query));
    }


    /**
     * 新增
     *
     * @param ${className}BO ${functionName}
     * @return Json<String>
     * @author ${author}
     * @methodName save
     * @time ${date}
     */
    @PreventRepeat
    @Methods(methodsName = "${functionName}新增", methods = "save")
    @ApiOperation(value = "${functionName}新增", httpMethod = "POST", notes = "${functionName}新增", response = Json.class)
    @ApiOperationSupport(ignoreParameters = {"id", "createDateTime", "createName", "modifyDateTime", "modifyName", "isDelete", "version"})
    @PostMapping("/save")
    public Json<String> save(@Validated(value = ${ClassName}BO.Save.class) @RequestBody ${ClassName}BO ${className}BO, HttpServletRequest httpServletRequest) {
        //缓存取出用户
        Map<String, Object> u = RedisUtils.getToken(stringRedisTemplate, httpServletRequest);
        if (u != null) {
            ${className}BO.setCreateName(u.get("name") + "-" + u.get("id"));
        }
        //返回内容
        return new Json(ReturnCode.成功, ${className}Service.saveEnhance(${className}BO));
    }


    /**
     * 修改
     *
     * @param ${className}BO ${functionName}
     * @return Json<Boolean>
     * @author ${author}
     * @methodName update
     * @time ${date}
     */
    @PreventRepeat
    @Methods(methodsName = "${functionName}修改", methods = "update")
    @ApiOperation(value = "${functionName}修改", httpMethod = "PUT", notes = "${functionName}修改", response = Json.class)
    @ApiOperationSupport(ignoreParameters = {"createDateTime", "createName", "modifyDateTime", "modifyName", "isDelete", "version"})
    @PutMapping("/update")
    public Json<Boolean> update(@Validated(value = ${ClassName}BO.Update.class) @RequestBody ${ClassName}BO ${className}BO, HttpServletRequest httpServletRequest) {
        //缓存取出用户
        Map<String, Object> u = RedisUtils.getToken(stringRedisTemplate, httpServletRequest);
        if (u != null) {
            ${className}BO.setModifyName(u.get("name") + "-" + u.get("id"));
        }
        //返回内容
        return new Json(ReturnCode.成功, ${className}Service.updateEnhance(${className}BO));
    }


    /**
     * 删除
     *
     * @param ${className}BO ${functionName}
     * @return Json<Boolean>
     * @author ${author}
     * @methodName remove
     * @time ${date}
     */
    @Methods(methodsName = "${functionName}删除", methods = "remove")
    @ApiOperation(value = "${functionName}删除", httpMethod = "DELETE", notes = "${functionName}删除", response = Json.class)
    @ApiOperationSupport(includeParameters = {"id"})
    @DeleteMapping("/remove")
    public Json<Boolean> remove(@Validated(value = ${ClassName}BO.Remove.class) @RequestBody ${ClassName}BO ${className}BO) {
        return new Json(ReturnCode.成功, ${className}Service.removeEnhance(${className}BO));
    }
}