package com.wuledi.metasearch.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuledi.article.model.dto.ArticleDTO;
import com.wuledi.article.model.dto.request.ArticleQueryRequest;
import com.wuledi.common.constant.SearchConstant;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.util.ThrowUtils;
import com.wuledi.metasearch.datasource.DataSource;
import com.wuledi.metasearch.datasource.DataSourceFactory;
import com.wuledi.metasearch.datasource.impl.ArticleDataSource;
import com.wuledi.metasearch.datasource.impl.PictureDataSource;
import com.wuledi.metasearch.datasource.impl.QuestionDataSource;
import com.wuledi.metasearch.datasource.impl.UserDataSource;
import com.wuledi.metasearch.model.dto.PictureDTO;
import com.wuledi.metasearch.model.dto.SearchDTO;
import com.wuledi.metasearch.model.dto.SearchRequest;
import com.wuledi.question.model.dto.QuestionDTO;
import com.wuledi.question.model.dto.request.QuestionQueryRequest;
import com.wuledi.user.model.dto.UserDTO;
import com.wuledi.user.model.dto.request.UserQueryRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * 搜索门面
 */
@Component
@Slf4j
public class SearchFacade {

    @Resource
    private ArticleDataSource articleDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private QuestionDataSource questionDataSource;

    @Resource
    private DataSourceFactory dataSourceFactory;


    public SearchDTO searchAll(SearchRequest searchRequest) {
        String type = searchRequest.getType(); // 搜索类型
        ThrowUtils.throwIf(type == null, ErrorCode.PARAMS_ERROR); // 如果搜索类型为空，则抛出异常
        String keyword = searchRequest.getKeyword(); // 搜索关键词
        long pageNumber = searchRequest.getPageNumber(); // 当前页码
        long pageSize = searchRequest.getPageSize(); // 每页大小

        // 如果搜索类型为空，则搜索所有类型
        if (type.isEmpty()) {
            // 异步搜索
            CompletableFuture<Page<UserDTO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest(); // 创建一个UserQueryRequest对象
                userQueryRequest.setNickname(keyword); // 设置昵称
                return userDataSource.doSearch(keyword, pageNumber, pageSize); // 返回用户列表
            });

            // 异步搜索
            CompletableFuture<Page<ArticleDTO>> articleTask = CompletableFuture.supplyAsync(() -> {
                ArticleQueryRequest articleQueryRequest = new ArticleQueryRequest(); // 创建一个ArticleQueryRequest对象
                articleQueryRequest.setSearchText(keyword); // 设置搜索关键词
                return articleDataSource.doSearch(keyword, pageNumber, pageSize); // 返回文章列表
            });


            // 异步搜索
            CompletableFuture<Page<PictureDTO>> pictureTask = CompletableFuture.supplyAsync(
                    () -> pictureDataSource.doSearch(keyword, pageNumber, pageSize));

            // 异步搜索
            CompletableFuture<Page<QuestionDTO>> questionTask = CompletableFuture.supplyAsync(() -> { // 返回题目列表
                QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
                questionQueryRequest.setTitle(keyword);
                return questionDataSource.doSearch(keyword, pageNumber, pageSize);
            });


            // 等待所有任务完成
            CompletableFuture.allOf(userTask, articleTask, pictureTask, questionTask).join();
            try {

                SearchDTO searchDTO = new SearchDTO(); // 搜索结果
                searchDTO.setUserList(userTask.get().getRecords()); // 设置用户列表
                searchDTO.setArticleList(articleTask.get().getRecords()); // 设置文章列表
                searchDTO.setPictureList(pictureTask.get().getRecords()); // 设置图片列表
                searchDTO.setQuestionList(questionTask.get().getRecords()); // 设置题目列表
                return searchDTO; // 返回搜索结果
            } catch (Exception e) { // 异常处理
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            SearchDTO searchDTO = new SearchDTO(); // 搜索结果
            DataSource<?> dataSource = dataSourceFactory.getDataSourceByType(type); // 获取数据源
            Page<?> page = dataSource.doSearch(keyword, pageNumber, pageSize); // 搜索
            List<?> recordsList = page.getRecords();
            switch (type) {
                case SearchConstant.ARTICLE:
                    searchDTO.setArticleList((List<ArticleDTO>) recordsList); // 设置文章列表
                    break;
                case SearchConstant.USER:
                    searchDTO.setUserList((List<UserDTO>) recordsList); // 设置用户列表
                    break;
                case SearchConstant.PICTURE:
                    searchDTO.setPictureList((List<PictureDTO>) recordsList); // 设置图片列表
                    break;
                case SearchConstant.QUESTION:
                    searchDTO.setQuestionList((List<QuestionDTO>) recordsList); // 设置题目列表
                    break;
                default:

            }
            searchDTO.setTotal(page.getTotal()); // 设置总记录数
            return searchDTO; // 返回搜索结果
        }
    }
}
