package com.chaozhi.web.controller;

import com.chaozhi.web.common.RestResponse;
import com.chaozhi.web.common.UserContext;
import com.chaozhi.web.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @GetMapping("/info")
    public RestResponse<UserInfoVO> info() {
        return RestResponse.success(UserContext.getCurrentUser());
    }
}
