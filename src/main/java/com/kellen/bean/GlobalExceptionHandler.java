package com.kellen.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.kellen.utils.exception.GbwExceptionHandler;

/**
 * 异常增强
 *
 * @author sunkailun
 * @DateTime 2020/12/10  下午8:48
 * @email 376253703@qq.com
 * @phone 13777579028
 * @explain
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends GbwExceptionHandler{
}
