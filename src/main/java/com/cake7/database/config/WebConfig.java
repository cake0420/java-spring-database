package com.cake7.database.config;

import com.cake7.database.interceptor.AuthenticationInterceptor;
import com.cake7.database.interceptor.CsrfInterceptor;
import com.cake7.database.repository.UserSessionRepository;
import com.cake7.database.util.Convert;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer, HandlerInterceptor {
    private final UserSessionRepository userSessionRepository;
    private final Convert convert;

    public WebConfig(UserSessionRepository userSessionRepository, Convert convert) {
        this.userSessionRepository = userSessionRepository;
        this.convert = convert;
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setOrder(1);
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/asset/**")
                .addResourceLocations("/asset/"); // 또는 "classpath:/static/asset/"
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor(userSessionRepository, convert);
    }

    @Bean
    public CsrfInterceptor csrfInterceptor() {
        return new CsrfInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(csrfInterceptor())
                .addPathPatterns("/**");

        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/api/protected/**")  // 보호된 경로 패턴
                .excludePathPatterns("/api/sign-in", "/api/sign-up", "/api/sign-out");  // 제외할 경로
    }
}
