package com.wuledi.judge.model;


import com.wuledi.codesandbox.model.ExecutionResult;
import com.wuledi.question.model.dto.JudgeCase;
import com.wuledi.question.model.dto.QuestionDTO;
import com.wuledi.question.model.dto.QuestionSubmitDTO;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private ExecutionResult executionResult; // 判题信息

    private List<String> inputList; // 输入列表

    private List<String> outputList; // 输出列表

    private List<JudgeCase> judgeCaseList; // 判题用例列表

    private QuestionDTO questionDTO; // 题目

    private QuestionSubmitDTO questionSubmitDTO; // 题目提交

}
