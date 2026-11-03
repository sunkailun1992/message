package com.kellen.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.kellen.entity.EntityBase;
import com.kellen.message.entity.enums.MessageReadStateEnum;
import com.kellen.message.entity.enums.MessageSendStateEnum;
import com.kellen.message.entity.enums.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户消息实体。
 * <p>
 * 用户消息用于记录管理端发送给指定用户的系统提醒或正常消息。
 *
 * @author sunkailun
 * @className UserMessage
 */
@Getter
@Setter
@TableName("message_user_message")
@Schema(description = "用户消息实体")
public class UserMessage extends EntityBase {

    /**
     * 消息类型。
     */
    @Schema(description = "消息类型")
    private MessageTypeEnum messageType;

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
     * 读取状态。
     */
    @Schema(description = "读取状态")
    private MessageReadStateEnum readState;

    /**
     * 发送时间。
     */
    @Schema(description = "发送时间")
    private LocalDateTime sendDateTime;

    /**
     * 读取时间。
     */
    @Schema(description = "读取时间")
    private LocalDateTime readDateTime;
}
