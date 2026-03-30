package com.chaozhi.web.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父节点 ID，顶级为 0 */
    private Long parentId;

    /** 显示名称（中文标题） */
    private String menuName;

    /** Vue Router name */
    private String routeName;

    /** Vue Router path */
    private String routePath;

    /** 'BasicLayout' 或视图路径，如 /material/index */
    private String component;

    private String icon;

    private Integer sort;

    /** CATALOG / MENU / BUTTON */
    private String type;

    /** 绑定的权限码（页面级） */
    private String permissionCode;

    /** 1=启用 0=禁用 */
    private Integer status;

    private Integer keepAlive;

    private Integer hideInMenu;

    /** 是否固定在 tab 栏 */
    private Integer affixTab;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
