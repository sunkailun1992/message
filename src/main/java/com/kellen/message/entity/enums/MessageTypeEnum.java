package com.kellen.message.entity.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户消息类型枚举。
 *
 * @author sunkailun
 * @className MessageTypeEnum
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum implements IEnum<String> {

    /**
     * 系统提醒。
     */
    SYSTEM_NOTICE("SYSTEM_NOTICE", "系统提醒"),

    /**
     * 正常消息。
     */
    NORMAL_MESSAGE("NORMAL_MESSAGE", "正常消息");

    /**
     * 数据库存储值。
     */
    private final String value;

    /**
     * 前端展示说明。
     */
    private final String desc;
}
