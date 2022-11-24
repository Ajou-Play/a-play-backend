package com.paran.aplay.oci;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.paran.aplay.common.config.property.OciConfigProperties;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetBucketRequest;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.responses.GetBucketResponse;
import com.oracle.bmc.objectstorage.responses.GetNamespaceResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties(value = OciConfigProperties.class)
public class ObjectStorageTest {

    @Test
    public void Test() throws IOException {

        return;

        /*
        final String compartmentId = "null";
        final String bucketName = "";
        final String privateUrl = "";
        final String object = "";

        ConfigFileReader.ConfigFile config = ConfigFileReader.parse(privateUrl, "DEFAULT");

        AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(config);


        ObjectStorage client = new ObjectStorageClient(provider);
        client.setRegion(Region.AP_CHUNCHEON_1);

        System.out.println("Getting the namespace.");
        GetNamespaceResponse namespaceResponse = client.getNamespace(GetNamespaceRequest.builder().build());

        String namespaceName = namespaceResponse.getValue();

        System.out.println("Creating Get bucket request");
        List<GetBucketRequest.Fields> fieldsList = new ArrayList<>(2);
        fieldsList.add(GetBucketRequest.Fields.ApproximateCount);
        fieldsList.add(GetBucketRequest.Fields.ApproximateSize);
        GetBucketRequest request =
                GetBucketRequest.builder()
                        .namespaceName(namespaceName)
                        .bucketName(bucketName)
                        .fields(fieldsList)
                        .build();

        System.out.println("Fetching bucket details");
        GetBucketResponse response = client.getBucket(request);

        System.out.println("Bucket Name : " + response.getBucket().getName());
        System.out.println("Bucket Compartment : " + response.getBucket().getCompartmentId());
        System.out.println(
                "The Approximate total number of objects within this bucket : "
                        + response.getBucket().getApproximateCount());
        System.out.println(
                "The Approximate total size of objects within this bucket : "
                        + response.getBucket().getApproximateSize());
        */
    }

}