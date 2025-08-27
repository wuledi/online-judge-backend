package com.wuledi.metasearch.model.dto;

import com.wuledi.article.model.dto.ArticleDTO;
import com.wuledi.question.model.dto.QuestionDTO;
import com.wuledi.user.model.dto.UserDTO;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 聚合搜索
 *
 */
@Data
public class SearchDTO implements Serializable {

    private List<UserDTO> userList; // 用户列表

    private List<ArticleDTO> articleList; // 文章列表

    private List<PictureDTO> pictureList; // 图片列表

    private List<QuestionDTO> questionList;

    private List<?> dataList; // 数据列表

    private long total; // 数据总数

    @Serial
    private static final long serialVersionUID = 1L;

}
