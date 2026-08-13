package com.main.nexus_frontend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    // Registra o AuthInterceptor para interceptar apenas os caminhos que exigem login (/pro/**, /company/**, /admin/**, /matches/**) 
    // rotas como /, /auth/**, /public/** ficam de fora, acessíveis livremente. os endpoints de dados JSON dos BFF controllers 
    // (/app-api/chat/**, /notifications/**) não estão cobertos por este interceptor, eles fazem sua própria checagem manual de sessão dentro do método 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/pro/**", "/company/**", "/admin/**", "/matches/**");
    }
}
