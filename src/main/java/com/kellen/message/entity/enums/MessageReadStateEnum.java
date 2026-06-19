package com.kellen.message.entity.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户消息读取状态枚举。
 *
 * @author sunkailun
 * @className MessageReadStateEnum
 */
@Getter
@AllArgsConstructor
public enum MessageReadStateEnum implements IEnum<String> {

    /**
     * 未读。
     */
    UNREAD("UNREAD", "未读"),

    /**
     * 已读。
     */
    READ("READ", "已读");

    /**
     * 数据库存储值。
     */
    private final String value;

    /**
     * 前端展示说明。
     */
    private final String desc;
}
