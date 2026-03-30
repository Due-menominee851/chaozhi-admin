package com.chaozhi.web.service.impl;

import com.chaozhi.web.entity.SysMenu;
import com.chaozhi.web.mapper.SysMenuMapper;
import com.chaozhi.web.service.SysMenuService;
import com.chaozhi.web.vo.SysMenuRouteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenuRouteVO> getMenuTreeByUserId(Long userId) {
        List<SysMenu> menus = sysMenuMapper.findByUserId(userId);
        return buildTree(menus, 0L);
    }

    private List<SysMenuRouteVO> buildTree(List<SysMenu> menus, Long parentId) {
        List<SysMenuRouteVO> result = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                SysMenuRouteVO vo = toRouteVO(menu);
                List<SysMenuRouteVO> children = buildTree(menus, menu.getId());
                if (!children.isEmpty()) {
                    vo.setChildren(children);
                }
                result.add(vo);
            }
        }
        return result;
    }

    private SysMenuRouteVO toRouteVO(SysMenu menu) {
        SysMenuRouteVO vo = new SysMenuRouteVO();
        vo.setPath(menu.getRoutePath());
        vo.setName(menu.getRouteName());
        vo.setComponent(menu.getComponent());

        SysMenuRouteVO.MetaVO meta = new SysMenuRouteVO.MetaVO();
        meta.setTitle(menu.getMenuName());
        meta.setIcon(menu.getIcon());
        meta.setOrder(menu.getSort());
        meta.setKeepAlive(menu.getKeepAlive() != null && menu.getKeepAlive() == 1);
        meta.setHideInMenu(menu.getHideInMenu() != null && menu.getHideInMenu() == 1);
        meta.setAffixTab(menu.getAffixTab() != null && menu.getAffixTab() == 1);
        vo.setMeta(meta);

        return vo;
    }
}
