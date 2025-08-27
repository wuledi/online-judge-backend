package com.wuledi.judge.strategy.impl;

import com.wuledi.codesandbox.model.ExecutionResult;
import com.wuledi.judge.model.JudgeContext;
import com.wuledi.judge.model.enums.JudgeInfoMessageEnum;
import com.wuledi.judge.strategy.JudgeStrategy;
import com.wuledi.question.model.dto.JudgeCase;
import com.wuledi.question.model.dto.JudgeConfig;
import com.wuledi.question.model.dto.JudgeInfo;
import com.wuledi.question.model.dto.QuestionDTO;

import java.util.List;

/**
 * Java 程序的判题策略
 */
public class PythonJudgeStrategy implements JudgeStrategy {

    /**
     * 执行判题
     *
     * @param judgeContext 判题上下文
     * @return 判题信息
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        // 获取判题信息
        ExecutionResult executionResult = judgeContext.getExecutionResult(); // 获取判题信息
        Long memory = executionResult.getMemoryUsageKB(); // 获取内存
        Long time = executionResult.getExecutionTimeMS(); // 获取时间
        List<String> inputList = judgeContext.getInputList(); // 获取输入
        List<String> outputList = judgeContext.getOutputList(); // 获取输出
        QuestionDTO questionDTO = judgeContext.getQuestionDTO(); // 获取题目
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList(); // 获取判题用例

        // 设置响应的判题信息
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED; // 默认为通过
        JudgeInfo judgeInfo = new JudgeInfo(); // 响应的判题信息
        judgeInfo.setMemory(memory); // 设置内存
        judgeInfo.setTime(time); // 设置时间

        // 先判断沙箱执行的结果输出数量是否和预期输出数量相等
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER; // 不通过
            judgeInfo.setMessage(judgeInfoMessageEnum.getValue()); // 设置判题信息
            return judgeInfo; // 返回判题信息
        }

        // 依次判断每一项输出和预期输出是否相等
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i); // 获取判题用例
            if (!judgeCase.getOutput().equals(outputList.get(i))) { // 不通过
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfo.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfo; // 返回判题信息
            }
        }

        // 判题配置限制
        JudgeConfig judgeConfig = questionDTO.getJudgeConfig(); // 获取判题配置
        Long needMemoryLimit = judgeConfig.getMemoryLimit(); // 获取内存限制
        Long needTimeLimit = judgeConfig.getTimeLimit(); // 获取时间限制

        // 输出消耗限制
        System.out.println("memory = " + memory);
        System.out.println("time = " + time);

        // Python 程序本身需要额外执行的时间和空间分别为默认策略的2倍和5倍
        if (memory > needMemoryLimit * 2) { // 内存超限,单位为KB
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfo.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfo;
        }
        if (time > needTimeLimit * 5) { // 时间超限,单位为ms
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED; // 时间超限
            judgeInfo.setMessage(judgeInfoMessageEnum.getValue()); // 设置判题信息
            return judgeInfo;
        }
        judgeInfo.setMessage(judgeInfoMessageEnum.getValue()); // 设置判题信息
        return judgeInfo;
    }
}
