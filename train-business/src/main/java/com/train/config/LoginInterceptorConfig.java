package com.train.config;

import com.train.common.interceptor.AccountInterceptor;
import com.train.common.interceptor.LogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginInterceptorConfig implements WebMvcConfigurer {

   @Autowired
   private LogInterceptor logInterceptor;

   @Autowired
   private AccountInterceptor accountInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(logInterceptor);

       // server.servlet.context-path 不要配置 需要排除的路径 是不能加上 context-path 的 不然会导致拦截器排除的路径不生效
       registry.addInterceptor(accountInterceptor)
               .addPathPatterns("/**")
               .excludePathPatterns(
                       "/account/login",
                       "/account/rendCode"
               );
   }
}
