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

       registry.addInterceptor(accountInterceptor)
               .addPathPatterns("/**")
               .excludePathPatterns(
                       "/system/management/account/login",
                       "/system/management/account/rendCode"
               );
   }
}
