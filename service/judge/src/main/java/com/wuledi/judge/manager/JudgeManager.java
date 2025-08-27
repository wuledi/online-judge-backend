package com.wuledi.judge.manager;


import com.wuledi.judge.model.JudgeContext;
import com.wuledi.judge.strategy.JudgeStrategy;
import com.wuledi.judge.strategy.impl.DefaultJudgeStrategy;
import com.wuledi.judge.strategy.impl.JavaJudgeStrategy;
import com.wuledi.judge.strategy.impl.PythonJudgeStrategy;
import com.wuledi.question.model.dto.JudgeInfo;
import com.wuledi.question.model.dto.QuestionSubmitDTO;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext 判题上下文
     * @return 判题信息
     */

    public JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmitDTO questionSubmitDTO = judgeContext.getQuestionSubmitDTO(); // 获取题目提交
        String language = questionSubmitDTO.getLanguage(); // 获取编程语言
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy(); // 默认的判题策略
        if ("java".equals(language)) { // 如果是 java 语言
            judgeStrategy = new JavaJudgeStrategy(); // 使用 java 语言的判题策略
        } else if ("python".equals(language)) { // 如果是 python 语言
            judgeStrategy = new PythonJudgeStrategy(); // 使用 python 语言的判题策略
        }
        return judgeStrategy.doJudge(judgeContext); // 执行判题
    }

}
