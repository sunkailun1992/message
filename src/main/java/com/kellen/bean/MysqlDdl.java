package com.kellen.bean;

import com.baomidou.mybatisplus.extension.ddl.IDdl;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * MyBatis-Plus自动维护DDL。
 * <p>
 * message 当前只保留DDL入口，后续新增消息业务表时再追加独立SQL脚本。
 *
 * @author sunkailun
 * @className MysqlDdl
 * @DateTime 2026/06/04
 * @email 376253703@qq.com
 */
@Component
public class MysqlDdl implements IDdl {

    /**
     * 当前项目数据源。
     */
    private final DataSource dataSource;

    /**
     * 构造MyBatis-Plus自动维护DDL组件。
     *
     * @param dataSource 当前项目数据源
     * @return void
     * @author sunkailun
     * @DateTime 2026/06/04
     * @email 376253703@qq.com
     */
    public MysqlDdl(DataSource dataSource) {
        this.dataSource = dataSource; // 保存当前应用数据源，交给MyBatis-Plus DDL运行器执行脚本。
    }

    /**
     * 指定执行脚本的数据源。
     *
     * @param consumer MyBatis-Plus DDL脚本执行器
     * @return void
     * @author sunkailun
     * @DateTime 2026/06/04
     * @email 376253703@qq.com
     */
    @Override
    public void runScript(Consumer<DataSource> consumer) {
        consumer.accept(dataSource); // 使用当前应用数据源执行DDL脚本，避免业务代码手写建表SQL。
    }

    /**
     * 获取自动维护DDL脚本列表。
     *
     * @return java.util.List<java.lang.String>
     * @author sunkailun
     * @DateTime 2026/06/04
     * @email 376253703@qq.com
     */
    @Override
    public List<String> getSqlFiles() {
        return Collections.emptyList(); // 当前消息模块暂无业务表初始化脚本，后续新增表时按AI规范追加独立SQL文件。
    }
}
