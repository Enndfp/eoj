package com.enndfp.eoj.model.dto.question;

import com.enndfp.eoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 题目查询请求
 *
 * @author Enndfp
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 题目标签列表
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 题目难度（0 - 简单、1 - 中等、2 - 困难）
     */
    private Integer difficulty;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建用户
     */
    private String creator;

    private static final long serialVersionUID = 1L;
}