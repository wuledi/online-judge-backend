package com.wuledi.article.job;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.wuledi.article.esdao.ArticleEsDTO;
import com.wuledi.article.esdao.ArticleEsDao;
import com.wuledi.article.model.entity.ArticleDO;
import com.wuledi.article.service.ArticleService;
import com.wuledi.common.util.JsonConverter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步文章到 es
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class FullSyncArticleToEs implements CommandLineRunner {

    @Resource
    private ArticleService articleService;

    @Resource
    private ArticleEsDao articleEsDao;


    @Override
    public void run(String... args) {
        List<ArticleDO> articleDOList = articleService.list(); // 从数据库中取出所有的文章
        if (CollectionUtils.isEmpty(articleDOList)) { // 如果文章列表为空,则返回
            return;
        }

        List<ArticleEsDTO> articleEsDTOList = articleDOList
                .stream() // 转换为流
                .map((ArticleDO articleDO)-> {
                    ArticleEsDTO articleEsDTO = new ArticleEsDTO();
                    BeanUtils.copyProperties(articleDO, articleEsDTO);
                    String tagsStr = articleDO.getTags();
                    if (StringUtils.isNotBlank(tagsStr)) {

//                        articleEsDTO.setTags(JSONUtil.toList(tagsStr, String.class));
                        articleEsDTO.setTags(JsonConverter.jsonToList(tagsStr));
                    }
                    return articleEsDTO;
                }) // 将 Article 对象转换为 ArticleEsDTO 对象
                .collect(Collectors.toList()); // 将 ArticleEsDTO 对象的列表转换为列表

        final int pageSize = 500; // 每次批量处理的数量
        int total = articleEsDTOList.size(); // 总数量
        log.info("FullSyncArticleToEs start, total {}", total); // 打印日志
        for (int i = 0; i < total; i += pageSize) { // 每次批量处理 pageSize 个元素
            int end = Math.min(i + pageSize, total); // 计算结束位置
            log.info("sync from {} to {}", i, end);
            articleEsDao.saveAll(articleEsDTOList.subList(i, end)); // 批量保存到 es
        }
        log.info("FullSyncArticleToEs end, total {}", total); // 打印日志
    }
}
