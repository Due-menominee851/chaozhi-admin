package com.chaozhi.web.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoVO {
    private String userId;
    private String username;
    private String realName;
    private String avatar;
    private String desc;
    private String homePath;
    private String token;
    private List<String> roles;
    /** 用户拥有的权限码列表，登录时写入 Redis，供 PermissionAspect 校验 */
    private List<String> permissionCodes;
}
