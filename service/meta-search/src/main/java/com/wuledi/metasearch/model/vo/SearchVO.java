package com.wuledi.metasearch.model.vo;

import com.wuledi.article.model.dto.ArticleDTO;
import com.wuledi.article.model.vo.ArticleVO;
import com.wuledi.metasearch.model.dto.PictureDTO;
import com.wuledi.metasearch.model.dto.SearchDTO;
import com.wuledi.question.model.dto.QuestionDTO;
import com.wuledi.question.model.vo.QuestionVO;
import com.wuledi.user.model.dto.UserDTO;
import com.wuledi.user.model.vo.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 聚合搜索
 */
@Data
public class SearchVO implements Serializable {

    private List<UserVO> userList; // 用户列表
    private List<ArticleVO> articleList; // 文章列表
    private List<PictureVO> pictureList; // 图片列表
    private List<QuestionVO> questionList; // 问题列表
    private List<?> dataList; // 数据列表
    private long total; // 数据总数

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 搜索DTO -> 搜索VO
     *
     * @param searchDTO 搜索DTO
     * @return 搜索VO
     */
    public static SearchVO fromDTO(SearchDTO searchDTO) {
        if (searchDTO == null) {
            return null;
        }
        SearchVO searchVO = new SearchVO();

        // 转换用户列表
        if (searchDTO.getUserList() != null) {
            searchVO.setUserList(searchDTO.getUserList().stream()
                    .map((UserDTO userDTO) -> {
                        UserVO userVO = new UserVO();
                        BeanUtils.copyProperties(userDTO, userVO);
                        return userVO;
                    })
                    .collect(Collectors.toList()));
        }

        // 转换文章列表
        if (searchDTO.getArticleList() != null) {
            searchVO.setArticleList(searchDTO.getArticleList().stream()
                    .map((ArticleDTO articleDTO)->{
                        ArticleVO articlePageVO = new ArticleVO();
                        BeanUtils.copyProperties(articleDTO, articlePageVO);
                        return articlePageVO;
                    })
                    .collect(Collectors.toList()));
        }

        // 转换图片列表（假设 PictureDTO 和 PictureVO 有对应的转换方法）
        if (searchDTO.getPictureList() != null) {
            searchVO.setPictureList(searchDTO.getPictureList().stream()
                    .map((PictureDTO pictureDTO) -> {
                        PictureVO pictureVO = new PictureVO();
                        BeanUtils.copyProperties(pictureDTO, pictureVO);
                        return pictureVO;
                    })
                    .collect(Collectors.toList()));
        }

        // 转换问题列表
        if (searchDTO.getQuestionList() != null) {
            searchVO.setQuestionList(searchDTO.getQuestionList().stream()
                    .map((QuestionDTO questionDTO)->{
                        // 拷贝
                        QuestionVO questionPageVO = new QuestionVO();
                        BeanUtils.copyProperties(questionDTO, questionPageVO);
                        return questionPageVO;
                    })
                    .collect(Collectors.toList()));
        }

        // 数据列表
        searchVO.setDataList(searchDTO.getDataList());

        // 总数
        searchVO.setTotal(searchDTO.getTotal());

        return searchVO;
    }
}