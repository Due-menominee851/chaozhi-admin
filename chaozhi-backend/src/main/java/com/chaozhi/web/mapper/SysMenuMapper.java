package com.chaozhi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaozhi.web.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("SELECT DISTINCT m.* FROM sys_menu m " +
            "JOIN sys_role_menu rm ON rm.menu_id = m.id " +
            "JOIN sys_user_role ur ON ur.role_id = rm.role_id " +
            "WHERE ur.user_id = #{userId} AND m.status = 1 " +
            "ORDER BY m.sort ASC")
    List<SysMenu> findByUserId(@Param("userId") Long userId);
}
