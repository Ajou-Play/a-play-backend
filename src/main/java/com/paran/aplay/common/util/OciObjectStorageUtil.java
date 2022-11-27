package com.paran.aplay.common.util;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetBucketRequest;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetBucketResponse;
import com.oracle.bmc.objectstorage.responses.GetNamespaceResponse;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import com.paran.aplay.team.domain.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OciObjectStorageUtil {

    private static final String BUCKET_NAME = "aplay";
    public static final String OBJECT_STORAGE_SERVER_URL = "https://objectstorage.ap-chuncheon-1.oraclecloud.com/p/t1rIkBURO2oMlHCn9XKTFP0y_YcHiPCjF-5GsBGdnMW9tATZ6Meq1rLBXHu1ejjT/n/axgga8ceqe0b/b/aplay/o/";
    public static final String TEAM_PROFILE_IMAGE_PREFIX = "image/team/";;
    private ObjectStorage client;
    private String bucketName;
    private String namespaceName;


    @PostConstruct
    protected void init() throws IOException {
        ClassPathResource configResource = new ClassPathResource("oci_config");
        ClassPathResource privateKeyResource = new ClassPathResource("oci_api_key.pem");
        ConfigFileReader.ConfigFile config = ConfigFileReader.parse(configResource.getInputStream(), "DEFAULT");

        File oci_config = File.createTempFile("oci_config","");
        oci_config.deleteOnExit();

        BufferedWriter bw = new BufferedWriter(new FileWriter(oci_config));
        bw.write(
                "[DEFAULT]\n" +
                "user=" + config.get("user") + "\n" +
                "fingerprint=" + config.get("fingerprint") + "\n" +
                "tenancy=" + config.get("tenancy") + "\n" +
                "region=" + config.get("region") + "\n" +
                "key_file=" + privateKeyResource.getURL().getFile()
        );
        bw.close();

        config = ConfigFileReader.parse(oci_config.getPath(), "DEFAULT");
        AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(config);

        client = new ObjectStorageClient(provider);
        client.setRegion(Region.AP_CHUNCHEON_1);
        GetNamespaceResponse namespaceResponse = client.getNamespace(GetNamespaceRequest.builder().build());

        namespaceName = namespaceResponse.getValue();
        bucketName = BUCKET_NAME;

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
