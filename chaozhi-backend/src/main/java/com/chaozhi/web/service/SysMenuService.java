package com.chaozhi.web.service;

import com.chaozhi.web.vo.SysMenuRouteVO;

import java.util.List;

public interface SysMenuService {

    List<SysMenuRouteVO> getMenuTreeByUserId(Long userId);
}
