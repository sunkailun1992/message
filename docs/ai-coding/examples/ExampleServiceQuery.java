package com.kellen.example.service.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kellen.example.entity.ExampleEntity;
import com.kellen.example.entity.query.ExampleQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 示例业务 Service 查询增强层。
 * <p>
 * 自动查询条件统一放在该类，ServiceImpl 只负责调用，不直接堆查询细节。
 *
 * @author sunkailun
 * @className ExampleServiceQuery
 * @time 2026/05/26
 */
@Service
public class ExampleServiceQuery {

    /**
     * 默认排序字段。
     */
    private static final String DEFAULT_SORT_FIELD = "create_date_time";

    /**
     * 构建查询条件。
     * <p>
     * QueryWrapper 已经通过 new QueryWrapper<>(entity) 承接 Entity 同名字段等值查询；
     * 本方法只补充排序、显示字段等无法由 Entity 自动表达的通用查询条件。
     *
     * @param exampleQuery 查询参数
     * @param queryWrapper 查询包装器
     * @return 查询包装器
     * @author sunkailun
     * @DateTime 2026/05/27
     * @email 376253703@qq.com
     */
    public QueryWrapper<ExampleEntity> query(ExampleQuery exampleQuery, QueryWrapper<ExampleEntity> queryWrapper) {
        // 查询对象为空时直接返回原包装器，避免示例生成空指针代码。
        if (exampleQuery == null) {
            // 返回调用方传入的 QueryWrapper。
            return queryWrapper;
        }

        // 计算排序字段，前端未传时使用默认创建时间字段。
        String sortField = StringUtils.defaultIfBlank(exampleQuery.getCollationFields(), DEFAULT_SORT_FIELD);
        // 判断是否显式要求升序。
        if (Boolean.TRUE.equals(exampleQuery.getCollation())) {
            // 拼接升序排序。
            queryWrapper.orderByAsc(sortField);
        } else {
            // 未传排序方向或传 false 时统一按降序排序。
            queryWrapper.orderByDesc(sortField);
        }

        // 处理显示字段。
        if (StringUtils.isNotBlank(exampleQuery.getFields())) {
            // 指定 select 字段。
            queryWrapper.select(exampleQuery.getFields());
        }

        // 返回查询包装器。
        return queryWrapper;
    }
}
