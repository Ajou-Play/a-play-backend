package com.paran.aplay.common.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class ObjectStorageUtil {

    
    @Getter
    final String bucketName = "aplay";
    @Getter
    final String namespaceName = "test";

}