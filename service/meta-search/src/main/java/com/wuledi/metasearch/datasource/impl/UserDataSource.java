package com.wuledi.metasearch.datasource.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuledi.common.constant.SearchConstant;
import com.wuledi.metasearch.datasource.DataSource;
import com.wuledi.user.model.dto.UserDTO;
import com.wuledi.user.model.dto.request.UserQueryRequest;
import com.wuledi.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 用户数据源
 */
@Service
@Slf4j
public class UserDataSource implements DataSource<UserDTO> {

    @Resource
    private UserService userService;

    /**
     * 用户数据源
     *
     * @param keyword    搜索关键词
     * @param pageNumber 页码
     * @param pageSize   每页大小
     * @return 用户列表
     */
    @Override
    public Page<UserDTO> doSearch(String keyword, Long pageNumber, Long pageSize) {
        UserQueryRequest userQueryRequest = new UserQueryRequest(); // 创建一个UserQueryRequest对象
        userQueryRequest.setNickname(keyword); // 设置昵称
        userQueryRequest.setPageNumber(pageNumber); // 设置当前页码
        userQueryRequest.setPageSize(pageSize); // 设置每页大小
        return userService.pageUsers(userQueryRequest); // 返回用户列表
    }

    /**
     * 获取数据源类型
     *
     * @return 数据源类型
     */
    @Override
    public String getType() {
        return SearchConstant.USER;
    }
}
