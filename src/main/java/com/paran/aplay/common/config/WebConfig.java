package com.paran.aplay.common.config;


import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.paran.aplay.common.filter.ExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final ObjectMapper objectMapper;

  private final ExceptionHandlerFilter exceptionHandlerFilter;

  @PostConstruct
  public void initObjectMapper() {
    objectMapper.getFactory().configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);
    objectMapper.registerModule(new JavaTimeModule());
  }

  // 프록시 설정 전까지 일단 도메인들 설정.
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/v1/**")
        .allowedOrigins("http://localhost:3000")
        .allowedMethods(
            HttpMethod.GET.name(),
            HttpMethod.HEAD.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.PATCH.name())
        .allowCredentials(true);
  }
}
