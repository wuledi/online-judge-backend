package com.wuledi.judge.service.impl;

import com.wuledi.codesandbox.model.ExecutionResult;
import com.wuledi.codesandbox.model.enums.ExecutionStateEnum;
import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.request.ExecuteCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;
import com.wuledi.codesandbox.model.response.ExecuteCodeResponse;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.util.JsonConverter;
import com.wuledi.judge.manager.JudgeManager;
import com.wuledi.judge.model.JudgeContext;
import com.wuledi.judge.model.enums.JudgeInfoMessageEnum;
import com.wuledi.judge.service.InvokeCodeSandboxService;
import com.wuledi.judge.service.JudgeService;
import com.wuledi.question.model.dto.JudgeCase;
import com.wuledi.question.model.dto.JudgeInfo;
import com.wuledi.question.model.dto.QuestionDTO;
import com.wuledi.question.model.dto.QuestionSubmitDTO;
import com.wuledi.question.model.entity.QuestionDO;
import com.wuledi.question.model.entity.QuestionSubmitDO;
import com.wuledi.question.model.enums.JudgeStatusEnum;
import com.wuledi.question.service.QuestionService;
import com.wuledi.question.service.QuestionSubmitService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目判题服务
 */
@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {

    @Resource // 注入题目服务
    private QuestionService questionService;

    @Resource // 注入题目提交服务
    private QuestionSubmitService questionSubmitService;

    @Resource // 注入判题管理器
    private JudgeManager judgeManager;

    @Resource
    private InvokeCodeSandboxService invokeCodeSandbox;


    @Override
    public void doJudge(long questionSubmitId) {

        // 依据题目提交 id 获取题目提交信息
        QuestionSubmitDTO questionSubmitDTO = questionSubmitService.getQuestionSubmit(questionSubmitId, null); // 获取题目提交信息
        if (questionSubmitDTO == null) { // 判空
            log.info("questionSubmit not found, questionSubmitId = {}", questionSubmitId);
            return;
        }

        // 依据题目 id 获取题目信息
        Long questionId = questionSubmitDTO.getQuestionId(); // 获取题目 id
        QuestionDTO questionDTO = questionService.getQuestion(questionId,null); // 获取题目信息
        if (questionDTO == null) { // 判空
            log.info("question not found, questionId = {}", questionId);
            return;
        }

        // 如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmitDTO.getStatus().equals(JudgeStatusEnum.WAITING.getValue())) {
            log.info("question submit status is not waiting, questionSubmitId = {}", questionSubmitId);
            return;
        }

        // 更改题目提交的状态为 “判题中”，防止重复执行
        QuestionSubmitDO questionSubmitDOUpdate = QuestionSubmitDO.builder()
                .id(questionSubmitId)// 设置题目提交 id
                .status(JudgeStatusEnum.RUNNING.getValue())// 设置题目提交状态为 “判题中”
                .build();// 创建题目提交信息
        boolean update = questionSubmitService.updateById(questionSubmitDOUpdate); // 更新题目提交信息
        if (!update) { // 更新失败
            log.error("question submit status update failed, questionSubmitId = {}", questionSubmitId);
            return;
        }


        // 获取构造执行代码的请求体需求参数
        String language = questionSubmitDTO.getLanguage(); // 获取编程语言
        String code = questionSubmitDTO.getCode(); // 获取代码
        List<JudgeCase> judgeCaseList = questionDTO.getJudgeCase(); // 获取判题输入用例字符串
        List<String> inputList = judgeCaseList // 获取判题输入用例列表
                .stream() // 流化处理
                .map(JudgeCase::getInput) // 将每个判题输入用例映射为输入字符串
                .collect(Collectors.toList()); // 转换为 List<String>
        // 构造执行代码的请求体
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .language(language) // 设置编程语言
                .code(code) // 设置代码
                .inputList(inputList) // 设置输入用例
                .build();

        // 调用代理代码沙箱的 executeCode 方法，输出日志并获取执行结果响应
        ExecuteCodeResponse executeCodeResponse = invokeCodeSandbox.executeCode(executeCodeRequest);

        // 获取沙箱的执行状态
        ExecutionStateEnum executeStatus = executeCodeResponse.getStatus(); // 获取沙箱的执行状态
        if (executeStatus != ExecutionStateEnum.SUCCESS) { // 沙箱执行失败
            log.error("question submit execute failed, questionSubmitId = {}", questionSubmitId);
            // 修改数据库中的判题结果
            questionSubmitDOUpdate = QuestionSubmitDO.builder()
                    .id(questionSubmitId) // 设置题目提交 id
                    .status(JudgeStatusEnum.FAILED.getValue())// 设置题目提交状态为 “判题失败”
                    .build(); // 更新题目提交信息
            // 构造判题信息信息
            ExecutionResult executionResult = ExecutionResult.builder()
                    .executionLog(JudgeInfoMessageEnum.COMPILE_ERROR.getText()) // 设置判题信息
                    .build();
            questionSubmitDOUpdate.setJudgeInfo(JsonConverter.objToJson(executionResult)); // 设置题目提交信息
            update = questionSubmitService.updateById(questionSubmitDOUpdate); // 更新题目提交信息
            if (!update) { // 更新失败
                log.error("question submit update failed, questionSubmitId = {}", questionSubmitId);
            }
        } else {
            // 根据沙箱的执行结果，设置题目的判题状态和信息
            List<String> outputList = executeCodeResponse.getOutputList(); // 获取输出用例
            JudgeContext judgeContext = new JudgeContext(); // 创建判题上下文
            judgeContext.setExecutionResult(executeCodeResponse.getExecutionResult()); // 设置判题信息
            judgeContext.setInputList(inputList); // 设置输入列表
            judgeContext.setOutputList(outputList); // 设置输出列表
            judgeContext.setJudgeCaseList(judgeCaseList); // 设置输入用例列表
            judgeContext.setQuestionDTO(questionDTO); // 设置题目
            judgeContext.setQuestionSubmitDTO(questionSubmitDTO); // 设置题目提交

            // 使用判题管理器进行判题(依据不同的语言，使用不同的判题策略)
            JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

            // 修改数据库中的判题结果
            questionSubmitDOUpdate = QuestionSubmitDO.builder()
                    .id(questionSubmitId)
                    .status(JudgeStatusEnum.SUCCEED.getValue())
                    .judgeInfo(JsonConverter.objToJson(judgeInfo))
                    .build();
            ; // 更新题目提交信息


            update = questionSubmitService.updateById(questionSubmitDOUpdate); // 更新题目提交信息
            if (!update) { // 更新失败
                log.error("question submit update failed, questionSubmitId = {}", questionSubmitId);
            }

            // 如果回答正确，更新通过数
            if (judgeInfo.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getValue())) {
                QuestionDO questionDOUpdate = QuestionDO.builder()
                        .id(questionId)
                        .acceptedNumber(questionDTO.getAcceptedNumber() + 1)// 设置题目通过数
                        .build();// 创建题目信息
                boolean updateQuestion = questionService.updateById(questionDOUpdate);
                if (!updateQuestion) { // 更新失败
                    log.error("question update failed, questionId = {}", questionId);
                }
            }

        }

    }

    /**
     * 代码调试
     *
     * @param request 请求体
     * @return 代码输出
     */
    @Override
    public DebugCodeResponse doDebug(DebugCodeRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return invokeCodeSandbox.debugCode(request); // 调用代理代码沙箱的 executeCode 方法，输出日志并获取执行结果响应
    }
}
