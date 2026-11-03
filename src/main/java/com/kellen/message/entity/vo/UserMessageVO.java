package com.kellen.message.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kellen.message.entity.enums.MessageReadStateEnum;
import com.kellen.message.entity.enums.MessageSendStateEnum;
import com.kellen.message.entity.enums.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户消息响应对象。
 *
 * @author sunkailun
 * @className UserMessageVO
 */
@Data
@Schema(description = "用户消息响应对象")
public class UserMessageVO implements Serializable {

    /**
     * 消息主键。
     */
    @Schema(description = "消息主键")
    private String id;

    /**
     * 租户ID。
     */
    @Schema(description = "租户ID")
    private String tenantId;

    /**
     * 消息类型。
     */
    @Schema(description = "消息类型")
    private MessageTypeEnum messageType;

    /**
     * 消息类型说明。
     */
    @Schema(description = "消息类型说明")
    private String messageTypeDesc;

    /**
     * 发送人用户ID。
     */
    @Schema(description = "发送人用户ID")
    private String senderUserId;

    /**
     * 发送人名称。
     */
    @Schema(description = "发送人名称")
    private String senderName;

    /**
     * 接收人用户ID。
     */
    @Schema(description = "接收人用户ID")
    private String receiverUserId;

    /**
     * 负责人用户ID。
     */
    @Schema(description = "负责人用户ID")
    private String ownerUserId;

    /**
     * 归属部门ID。
     */
    @Schema(description = "归属部门ID")
    private String deptId;

    /**
     * 消息标题。
     */
    @Schema(description = "消息标题")
    private String title;

    /**
     * 消息内容。
     */
    @Schema(description = "消息内容")
    private String content;

    /**
     * 发送状态。
     */
    @Schema(description = "发送状态")
    private MessageSendStateEnum sendState;

    /**
     * 发送状态说明。
     */
    @Schema(description = "发送状态说明")
    private String sendStateDesc;

    /**
     * 读取状态。
     */
    @Schema(description = "读取状态")
    private MessageReadStateEnum readState;

    /**
     * 读取状态说明。
     */
    @Schema(description = "读取状态说明")
    private String readStateDesc;

    /**
     * 发送时间。
     */
    @Schema(description = "发送时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime sendDateTime;

    /**
     * 读取时间。
     */
    @Schema(description = "读取时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime readDateTime;

    /**
     * 数据库版本号。
     */
    @Schema(description = "数据库版本号")
    private Integer version;
}
