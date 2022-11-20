package com.paran.aplay.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.oci")
@Getter
@Setter
public class OciConfig {
    private String tenecyId;
}
