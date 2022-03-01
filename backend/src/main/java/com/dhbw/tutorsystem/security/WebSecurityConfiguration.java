package com.dhbw.tutorsystem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class WebSecurityConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    @Bean
    public ClassLoaderTemplateResolver customTemplateResolver() {
        ClassLoaderTemplateResolver configurer = new ClassLoaderTemplateResolver();
        configurer.setPrefix("mail-templates/");
        configurer.setSuffix(".html");
        configurer.setTemplateMode(TemplateMode.HTML);
        configurer.setCharacterEncoding("UTF-8");
        configurer.setOrder(0); // this is important. This way spring boot will listen to both places 0 and 1
        configurer.setCheckExistence(true);
        return configurer;
    }

    // @Bean
    // public SpringTemplateEngine thymeleafTemplateEngine(ITemplateResolver templateResolver) {
    //     SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    //     templateEngine.setTemplateResolver(templateResolver);
    //     templateEngine.setTemplateEngineMessageSource(emailMessageSource());
    //     return templateEngine;
    // }
}
