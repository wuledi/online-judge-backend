package com.wuledi.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuledi.article.model.entity.ArticleDO;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* @author wuledi
* @description 针对表【article(文章)】的数据库操作Mapper
* @createDate 2025-03-11 16:17:03
* @Entity com.wuledi.model.entity.Article
*/
public interface ArticleMapper extends BaseMapper<ArticleDO> {
    /**
     * 查询文章列表（包括已被删除的数据）
     */
    @Select("select * from article where update_time >= #{minUpdateTime}")
    List<ArticleDO> listArticleWithDelete(Date minUpdateTime);

}




