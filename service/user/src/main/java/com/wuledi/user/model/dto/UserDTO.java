package com.wuledi.user.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户
 *
 * @TableName user
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别（0-未知，1-男，2-女）
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 简介
     */
    private String profile;

    /**
     * 微信ID
     */
    private String unionId;

    /**
     * 公众号OpenID
     */
    private String mpOpenId;

    /**
     * 加密Access Key
     */
    private String accessKey;

    /**
     * 加密Secret Key
     */
    private String secretKey;

    /**
     * 角色（0-用户，1-管理员）
     */
    private Integer role;

    /**
     * 状态(0-正常，1-冻结)
     */
    private Integer status;

    /**
     * 用户类型
     */
    private String type;

    /**
     * 标签 json 列表
     */
    private List<String> tags;

    /**
     * 徽章
     */
    private List<String> badge;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    /**
     * 是否删除
     */
    private Integer isDelete;
}