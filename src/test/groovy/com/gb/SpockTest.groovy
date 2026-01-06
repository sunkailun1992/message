package com.gb

import com.gb.ApiApplication
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

/**
 * @ClassName com.gb.SpockTest
 * @Description ${DESC}* @Author 孙凯伦
 * @Mobile 13777579028
 * @Email 376253703@qq.com
 * @Time 2021/12/17 3:45 PM
 */

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ApiApplication.class)
class SpockTest extends Specification {
}
