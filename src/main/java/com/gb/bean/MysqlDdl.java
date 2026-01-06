package com.gb.bean;

import mybatis.mate.ddl.SimpleDdl;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName MysqlDdl
 * @Description 版本sql同步
 * @Author 孙凯伦
 * @Mobile 13777579028
 * @Email 376253703@qq.com
 * @Time 2021/12/1 3:33 PM
 */

@Component
public class MysqlDdl extends SimpleDdl {

    /**
     * 执行 SQL 脚本方式
     */
    @Override
    public List<String> getSqlFiles() {
        return Arrays.asList(
                // 内置包方式
//                "db/test.sql"
        );
    }
}

