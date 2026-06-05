package com.kellen.message.entity.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户消息发送状态枚举。
 *
 * @author sunkailun
 * @className MessageSendStateEnum
 * @DateTime 2026/06/04
 * @email 376253703@qq.com
 */
@Getter
@AllArgsConstructor
public enum MessageSendStateEnum implements IEnum<String> {

    /**
     * 已发送。
     */
    SENT("SENT", "已发送"),

    /**
     * 发送失败。
     */
    FAILED("FAILED", "发送失败");

    /**
     * 数据库存储值。
     */
    private final String value;

    /**
     * 前端展示说明。
     */
    private final String desc;
}
