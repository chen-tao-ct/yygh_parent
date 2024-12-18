package com.atguigu.yygh.order.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.atguigu.yygh.order.mapper")
public class OrderConfig {
  /** 分页插件 */
  @Bean
  public PaginationInterceptor paginationInterceptor() {
    return new PaginationInterceptor();
  }

  /** 对于逻辑删除拦截器的bean的注入方式 */
  @Bean
  public ISqlInjector sqlInjector() {
    return new LogicSqlInjector();
  }
}
