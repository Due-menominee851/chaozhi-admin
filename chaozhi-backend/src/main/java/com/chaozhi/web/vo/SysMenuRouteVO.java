package com.chaozhi.web.vo;

import lombok.Data;

import java.util.List;

/**
 * 菜单路由 VO，与前端 RouteRecordStringComponent 格式对齐
 */
@Data
public class SysMenuRouteVO {

    private String path;

    private String name;

    /** 'BasicLayout' 或视图路径，如 /material/index */
    private String component;

    private MetaVO meta;

    private List<SysMenuRouteVO> children;

    @Data
    public static class MetaVO {
        private String title;
        private String icon;
        private Integer order;
        private Boolean keepAlive;
        private Boolean hideInMenu;
        private Boolean affixTab;
    }
}
