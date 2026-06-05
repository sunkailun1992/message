package com.kellen.message.service.results;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kellen.message.entity.UserMessage;
import com.kellen.message.entity.vo.UserMessageVO;
import com.kellen.utils.convert.GeneralConvertor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 用户消息查询结果转换增强。
 *
 * @author sunkailun
 * @className UserMessageServiceResults
 * @DateTime 2026/06/04
 * @email 376253703@qq.com
 */
@Component
public class UserMessageServiceResults {

    /**
     * 转换单条用户消息结果。
     *
     * @param recordDO 用户消息实体
     * @return 用户消息响应对象
     */
    public UserMessageVO toVO(UserMessage recordDO) {
        if (recordDO == null) {
            return null; // 实体为空时返回空响应。
        }
        UserMessageVO vo = GeneralConvertor.convertor(recordDO, UserMessageVO.class); // 转换基础同名字段。
        vo.setMessageTypeDesc(recordDO.getMessageType() == null ? null : recordDO.getMessageType().getDesc()); // 补充消息类型说明。
        vo.setSendStateDesc(recordDO.getSendState() == null ? null : recordDO.getSendState().getDesc()); // 补充发送状态说明。
        vo.setReadStateDesc(recordDO.getReadState() == null ? null : recordDO.getReadState().getDesc()); // 补充读取状态说明。
        return vo; // 返回响应对象。
    }

    /**
     * 转换用户消息列表结果。
     *
     * @param recordsDO 用户消息实体列表
     * @return 用户消息响应列表
     */
    public List<UserMessageVO> toListVO(List<UserMessage> recordsDO) {
        if (recordsDO == null || recordsDO.isEmpty()) {
            return Collections.emptyList(); // 空列表统一返回空集合。
        }
        return recordsDO.stream().map(this::toVO).toList(); // 逐条转换为响应对象。
    }

    /**
     * 转换用户消息分页结果。
     *
     * @param pageDO 用户消息实体分页
     * @return 用户消息响应分页
     */
    public Page<UserMessageVO> toPageVO(Page<UserMessage> pageDO) {
        if (pageDO == null) {
            return new Page<>(); // 分页为空时返回空分页对象。
        }
        Page<UserMessageVO> pageVO = new Page<>(pageDO.getCurrent(), pageDO.getSize(), pageDO.getTotal()); // 创建响应分页。
        pageVO.setRecords(toListVO(pageDO.getRecords())); // 设置响应记录。
        return pageVO; // 返回响应分页。
    }

    /**
     * 增强用户消息列表结果。
     *
     * @param records 用户消息响应列表
     * @return 用户消息响应列表
     */
    public List<UserMessageVO> assignment(List<UserMessageVO> records) {
        return records == null ? Collections.emptyList() : records; // 当前暂无跨服务补全，统一返回非空列表。
    }

    /**
     * 增强用户消息分页结果。
     *
     * @param page 用户消息响应分页
     * @return 用户消息响应分页
     */
    public Page<UserMessageVO> assignment(Page<UserMessageVO> page) {
        return page; // 当前暂无跨服务补全，直接返回分页。
    }
}
