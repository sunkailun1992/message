package com.kellen.message.entity.query;

import com.kellen.message.entity.enums.MessageReadStateEnum;
import com.kellen.message.entity.enums.MessageSendStateEnum;
import com.kellen.message.entity.enums.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户消息查询参数。
 *
 * @author sunkailun
 * @className UserMessageQuery
 * @DateTime 2026/06/04
 * @email 376253703@qq.com
 */
@Data
@Schema(description = "用户消息查询参数")
public class UserMessageQuery implements Serializable {

    /**
     * 当前页码。
     */
    @Schema(description = "当前页码", example = "1")
    @Min(groups = {Select.class}, value = 1, message = "current最小为1")
    private Long current;

    /**
     * 每页数量。
     */
    @Schema(description = "每页数量", example = "10")
    @Min(groups = {Select.class}, value = 1, message = "size最小为1")
    private Long size;

    /**
     * 租户ID。
     */
    @Schema(description = "租户ID", example = "100")
    @NotBlank(message = "tenantId不能为空")
    @NotBlank(groups = {Select.class}, message = "tenantId不能为空")
    private String tenantId;

    /**
     * 消息主键。
     */
    @Schema(description = "消息主键")
    private String id;

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
     * 指定查询字段。
     */
    @Schema(description = "指定查询字段", example = "id,title,message_type,send_state,read_state,version")
    private String fields;

    /**
     * 是否升序。
     */
    @Schema(description = "是否升序，true为升序，false为降序", example = "false")
    private Boolean collation;

    /**
     * 排序字段。
     */
    @Schema(description = "排序字段", example = "send_date_time")
    private String collationFields;

    /**
     * 通用关键字。
     */
    @Schema(description = "通用关键字", example = "维护")
    private String query;

    /**
     * 是否执行结果增强。
     */
    @Schema(description = "是否执行结果增强", example = "true")
    private Boolean assignment;

    /**
     * 分页查询校验分组。
     */
    public interface Select {
    }
}
