package com.kellen.message.entity.bo;

import com.kellen.message.entity.enums.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户消息发送参数。
 *
 * @author sunkailun
 * @className UserMessageBO
 * @DateTime 2026/06/04
 * @email 376253703@qq.com
 */
@Data
@Schema(description = "用户消息发送参数")
public class UserMessageBO implements Serializable {

    /**
     * 租户ID。
     */
    @Schema(description = "租户ID", example = "100")
    @NotBlank(message = "tenantId不能为空")
    private String tenantId;

    /**
     * 接收人用户ID列表。
     */
    @Schema(description = "接收人用户ID列表", example = "[\"1000000000000000001\"]")
    @NotEmpty(message = "receiverUserIds不能为空")
    private List<String> receiverUserIds;

    /**
     * 消息类型。
     */
    @Schema(description = "消息类型", example = "SYSTEM_NOTICE")
    @NotNull(message = "messageType不能为空")
    private MessageTypeEnum messageType;

    /**
     * 消息标题。
     */
    @Schema(description = "消息标题", example = "系统维护提醒")
    @NotBlank(message = "title不能为空")
    private String title;

    /**
     * 消息内容。
     */
    @Schema(description = "消息内容", example = "系统将于今晚进行维护，请提前保存数据。")
    @NotBlank(message = "content不能为空")
    private String content;
}
