package com.whx.tmall.web;

import com.whx.tmall.pojo.User;
import com.whx.tmall.service.UserService;
import com.whx.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    //返回分页对象及其数据
    @GetMapping("/users")
    public Page4Navigator<User> list(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start < 0 ? 0 : start;
        Page4Navigator<User> page = userService.list(start, size, 5);
        return page;
    }

    /*
     * 增加交由前台用户注册功能
     * 删除不提供（用户信息是最重要的资料）
     * 修改不提供，应该由前台用户自己完成
     */

}
