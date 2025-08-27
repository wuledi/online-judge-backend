package com.wuledi.article.task;


import cn.hutool.json.JSONUtil;
import com.wuledi.article.esdao.ArticleEsDTO;
import com.wuledi.article.esdao.ArticleEsDao;
import com.wuledi.article.mapper.ArticleMapper;
import com.wuledi.article.model.entity.ArticleDO;
import com.wuledi.task.service.DynamicTask;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增量同步文章到 es
 */
@Component
@Slf4j
public class IncSyncArticleToEsTask implements DynamicTask {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private ArticleEsDao articleEsDao;


    /**
     * 每分钟执行一次
     * <p>
     * todo 分布式锁
     */
    //  @SchedulerLock(name = "IncSyncArticleToEsTask", lockAtLeastFor = "5000") // 锁住任务,防止并发执行
    @Override
    public void execute() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - 5 * 60 * 1000L);
        List<ArticleDO> articleDOList = articleMapper.listArticleWithDelete(fiveMinutesAgoDate);

        if (CollectionUtils.isEmpty(articleDOList)) { // 如果文章列表为空,则返回
            log.info("no inc article");
            return;
        }
        List<ArticleEsDTO> articleEsDTOList = articleDOList.stream()  // 转换为流
                .map((ArticleDO articleDO) -> {
                    ArticleEsDTO articleEsDTO = new ArticleEsDTO();
                    BeanUtils.copyProperties(articleDO, articleEsDTO);
                    String tagsStr = articleDO.getTags();
                    if (StringUtils.isNotBlank(tagsStr)) {
                        articleEsDTO.setTags(JSONUtil.toList(tagsStr, String.class));
                    }
                    return articleEsDTO;
                }) // 将 Article 对象转换为 ArticleEsDTO 对象
                .collect(Collectors.toList()); // 将 ArticleEsDTO 对象的列表转换为列表
        final int pageSize = 500; // 每次批量处理的数量
        int total = articleEsDTOList.size(); // 总数量
        log.info("IncSyncArticleToEs start, total {}", total); // 打印日志
        for (int i = 0; i < total; i += pageSize) { // 每次批量处理 pageSize 个元素
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            articleEsDao.saveAll(articleEsDTOList.subList(i, end));
        }
        log.info("IncSyncArticleToEs end, total {}", total);
    }


    @Override
    public String getTaskName() {
        return "IncSyncArticleToEsTask";
    }
}
