package com.wuledi.user.security;

import com.wuledi.user.model.entity.UserDO;
import com.wuledi.security.userdetails.UserDetailsImpl;
import com.wuledi.user.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用户信息服务
 *
 * @author wuledi
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService { // 实现全部的认证与授权数据加载
    @Resource
    private UserMapper userMapper;

    /**
     * 加载用户信息
     *
     * @param username 用户名
     * @return 用户信息
     * @throws UsernameNotFoundException 用户名不存在异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserDO> optional = Optional.ofNullable(userMapper.findByUsername(username));  // 数据查询
        if (optional.isEmpty()) {           // 用户名不存在
            log.error("用户名不存在");
            return null;
        }
        // 属性赋值
        UserDetails userDetails = new UserDetailsImpl();
        BeanUtils.copyProperties(optional.get(), userDetails); // 复制属性
        return userDetails; // 返回用户信息
    }

}