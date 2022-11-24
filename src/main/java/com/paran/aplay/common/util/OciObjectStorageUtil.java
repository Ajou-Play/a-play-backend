package com.paran.aplay.common.util;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.CreateMultipartUploadRequest;
import com.oracle.bmc.objectstorage.requests.GetBucketRequest;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetBucketResponse;
import com.oracle.bmc.objectstorage.responses.GetNamespaceResponse;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import com.paran.aplay.common.config.property.OciConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Null;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OciObjectStorageUtil {

    private final OciConfigProperties properties;
    private ObjectStorage client;
    private String bucketName;
    private String namespaceName;

    @PostConstruct
    protected void init() throws IOException {
        ConfigFileReader.ConfigFile config = ConfigFileReader.parse(properties.getConfigUrl(), "DEFAULT");
        AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(config);
        client = new ObjectStorageClient(provider);
        client.setRegion(Region.AP_CHUNCHEON_1);
        GetNamespaceResponse namespaceResponse = client.getNamespace(GetNamespaceRequest.builder().build());

        namespaceName = namespaceResponse.getValue();
        bucketName = properties.getBucketName();;

        connectTest();
    }

    private void connectTest() {
        List<GetBucketRequest.Fields> fieldsList = new ArrayList<>(2);
        fieldsList.add(GetBucketRequest.Fields.ApproximateCount);
        fieldsList.add(GetBucketRequest.Fields.ApproximateSize);
        GetBucketRequest request =
                GetBucketRequest.builder()
                        .namespaceName(namespaceName)
                        .bucketName(bucketName)
                        .fields(fieldsList)
                        .build();
        GetBucketResponse response = client.getBucket(request);

        Optional.of(response).ifPresentOrElse(
                getBucketResponse -> System.out.println("object-storage connect successful"),
                () -> System.err.println("object-storage connect failed")
        );
    }

    public Boolean postObject(String url, InputStream object) {

        PutObjectRequest req = PutObjectRequest.builder()
                .namespaceName(namespaceName)
                .bucketName(bucketName)
                .objectName(url)
                .body$(object)
                .build();
        PutObjectResponse res = client.putObject(req);

        Optional.of(res).ifPresentOrElse(
                putObjectResponse -> {
                    putObjectSuccess(putObjectResponse);
                },
                () -> {
                    System.err.println("put object error");
                }
        );

        return !Optional.of(res).isEmpty();
    }

    private void putObjectSuccess(PutObjectResponse res){
        //TODO Logging
    }



}
