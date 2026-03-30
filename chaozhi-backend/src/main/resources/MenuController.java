package com.xxx.xxx.controller;

import com.xxx.xxx.common.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 菜单接口 —— 返回静态菜单数据，后续替换为数据库
 */
@RestController
@RequestMapping("/menu")
@Slf4j
public class MenuController {

    @GetMapping("/all")
    public RestResponse<List<Map<String, Object>>> all() {
        // 拦截器已保证登录态，直接返回菜单

        List<Map<String, Object>> menus = new ArrayList<>();

        // Dashboard
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("name", "Dashboard");
        dashboard.put("path", "/dashboard");
        dashboard.put("redirect", "/analytics");
        Map<String, Object> dashMeta = new LinkedHashMap<>();
        dashMeta.put("order", -1);
        dashMeta.put("title", "page.dashboard.title");
        dashboard.put("meta", dashMeta);

        List<Map<String, Object>> dashChildren = new ArrayList<>();

        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("name", "Analytics");
        analytics.put("path", "/analytics");
        analytics.put("component", "/dashboard/analytics/index");
        Map<String, Object> analyticsMeta = new LinkedHashMap<>();
        analyticsMeta.put("affixTab", true);
        analyticsMeta.put("title", "page.dashboard.analytics");
        analytics.put("meta", analyticsMeta);
        dashChildren.add(analytics);

        Map<String, Object> workspace = new LinkedHashMap<>();
        workspace.put("name", "Workspace");
        workspace.put("path", "/workspace");
        workspace.put("component", "/dashboard/workspace/index");
        Map<String, Object> workspaceMeta = new LinkedHashMap<>();
        workspaceMeta.put("title", "page.dashboard.workspace");
        workspace.put("meta", workspaceMeta);
        dashChildren.add(workspace);

        dashboard.put("children", dashChildren);
        menus.add(dashboard);

        return RestResponse.success(menus);
    }
}
