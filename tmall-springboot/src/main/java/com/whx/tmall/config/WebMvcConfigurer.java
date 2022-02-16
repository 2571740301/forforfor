package com.whx.tmall.config;

import com.whx.tmall.interceptor.LoginInterceptor;
import com.whx.tmall.interceptor.OtherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
 class WebMvcConfigurer extends WebMvcConfigurerAdapter {
   
    @Bean
    public OtherInterceptor getOtherInterceptor(){
        return new OtherInterceptor();
    }
    @Bean
    public LoginInterceptor getLoginInterceptor(){
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加登录拦截器需要登录才可以访问的页面
        registry.addInterceptor(getLoginInterceptor())
                .addPathPatterns("/**");
        //追加渲染拦截器
        registry.addInterceptor(getOtherInterceptor())
                .addPathPatterns("/**");
    }
}
