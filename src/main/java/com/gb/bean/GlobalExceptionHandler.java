package com.gb.bean;

import cn.hutool.core.convert.Convert;
import com.gb.utils.Json;
import com.gb.utils.enumeration.ReturnCode;
import com.gb.utils.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

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
