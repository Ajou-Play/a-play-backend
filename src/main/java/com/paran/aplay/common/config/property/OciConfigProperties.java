package com.paran.aplay.common.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.oci")
@Getter
@Setter
public class OciConfigProperties {
    private String tenencyId;
    private String bucketName;
    private String configUrl;
}
