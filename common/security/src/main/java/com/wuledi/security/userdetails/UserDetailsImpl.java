package com.wuledi.security.userdetails;

import com.wuledi.security.enums.UserRoleEnum;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;


/**
 * 用户（认证）信息
 *
 * @author wuledi
 */
@Data
public class UserDetailsImpl implements UserDetails {    // 用户（认证）信息
    @Serial // 序列化版本号
    private static final long serialVersionUID = 1L;
    private Long id; // 用户id
    private String username;         // 用户名
    private String password;        // 密码
    private Integer role;             // 角色 0-ROLE_USER 1-ROLE_ADMIN
    private transient List<GrantedAuthority> roles;     // 角色列表


    public UserDetailsImpl() {
    }

    public UserDetailsImpl(Long id, String username, String password, Integer role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // 获取授权数据
        // 获取角色列表,将role按照0-ROLE_USER 1-ROLE_ADMIN的格式返回
        if (role == UserRoleEnum.USER.getCode()) {
            return List.of((GrantedAuthority) UserRoleEnum.USER::getAuthority);
        } else if (role == UserRoleEnum.ADMIN.getCode()) {
            return List.of((GrantedAuthority) UserRoleEnum.ADMIN::getAuthority);
        }
        return null;
    }


    @Override
    public String getUsername() {      // 返回用户名
        return this.username;
    }

    @Override
    public String getPassword() {      // 返回密码
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {    // 账户未过期
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {    // 账户未锁定
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {   // 认证未失效
        return true;
    }

    @Override
    public boolean isEnabled() {       // 启用状态
        return true;
    }
}