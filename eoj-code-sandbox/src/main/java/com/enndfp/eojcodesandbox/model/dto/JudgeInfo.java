package com.enndfp.eojcodesandbox.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 判题信息
 *
 * @author Enndfp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存
     */
    private Long memory;

    /**
     * 消耗时间
     */
    private Long time;
}
