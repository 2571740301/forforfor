package com.whx.tmall.realm;

import com.whx.tmall.pojo.User;
import com.whx.tmall.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

//Ps:JPARealm 这个类，用户提供，但是不由用户自己调用，而是由 Shiro 去调用
//shiro的中介域Realm 在项目中用于验证账号登录和授权、账号密码加密
public class JPARealm extends AuthorizingRealm {
    //重写其抽象类的抽象方法
    @Autowired
    UserService userService;

    //授权权限
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //能进入到这里，表示账号已经通过验证了, 直接返回一个授权对象即可
        SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();
        return s;
    }

    //验证授权
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userName = token.getPrincipal().toString();
        User user = userService.getByName(userName);
        String passwordInDB = user.getPassword();
        String salt = user.getSalt();
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName, passwordInDB, ByteSource.Util.bytes(salt), getName());
        return authenticationInfo;
    }
}
    