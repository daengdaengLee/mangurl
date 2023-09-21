package io.github.daengdaenglee.mangurl;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DownloadFileRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@SpringBootApplication
public class MangurlApplication {

    public static void main(String[] args) {
        MangurlApplication.downloadConfig();
        SpringApplication.run(MangurlApplication.class, args);
    }

    private static void downloadConfig() {
        var MANGURL_CONFIG_FILE_FROM = System.getenv("MANGURL_CONFIG_FILE_FROM");
        var MANGURL_CONFIG_FILE_TO = System.getenv("MANGURL_CONFIG_FILE_TO");
        var MANGURL_AWS_ACCESS_KEY_ID = System.getenv("MANGURL_AWS_ACCESS_KEY_ID");
        var MANGURL_AWS_SECRET_ACCESS_KEY = System.getenv("MANGURL_AWS_SECRET_ACCESS_KEY");
        if (MANGURL_CONFIG_FILE_FROM == null || MANGURL_CONFIG_FILE_TO == null) {
            return;
        }

        var s3Utilities = S3Utilities.builder().region(Region.AP_NORTHEAST_2).build();
        var s3Uri = s3Utilities.parseUri(URI.create(MANGURL_CONFIG_FILE_FROM));
        var region = s3Uri.region().orElseThrow(() -> new IllegalArgumentException("MANGURL_CONFIG_FILE_FROM URL 이 잘못되었습니다. region 을 알 수 없습니다."));
        var bucket = s3Uri.bucket().orElseThrow(() -> new IllegalArgumentException("MANGURL_CONFIG_FILE_FROM URL 이 잘못되었습니다. bucket 을 알 수 없습니다."));
        var key = s3Uri.key().orElseThrow(() -> new IllegalArgumentException("MANGURL_CONFIG_FILE_FROM URL 이 잘못되었습니다. key 를 알 수 없습니다."));


        var destinationPath = Paths.get(MANGURL_CONFIG_FILE_TO);
        try {
            Files.createDirectories(destinationPath.getParent());
        } catch (IOException e) {
            throw new RuntimeException("MANGURL_CONFIG_FILE_TO 위치에 디렉토리를 만들 수 없습니다.", e);
        }

        var s3ClientBuilder = S3AsyncClient.builder();
        if (MANGURL_AWS_ACCESS_KEY_ID != null && MANGURL_AWS_SECRET_ACCESS_KEY != null) {
            var awsBasicCredentials = AwsBasicCredentials.create(MANGURL_AWS_ACCESS_KEY_ID, MANGURL_AWS_SECRET_ACCESS_KEY);
            var staticCredentialsProvider = StaticCredentialsProvider.create(awsBasicCredentials);
            s3ClientBuilder.credentialsProvider(staticCredentialsProvider);
        }
        try (var s3Client = s3ClientBuilder.region(region).build();
             var s3TransferManager = S3TransferManager.builder().s3Client(s3Client).build()) {
            var downloadFileRequest = DownloadFileRequest.builder()
                    .getObjectRequest(req -> req.bucket(bucket).key(key))
                    .destination(destinationPath)
                    .addTransferListener(LoggingTransferListener.create())
                    .build();
            var download = s3TransferManager.downloadFile(downloadFileRequest);
            download.completionFuture().join();
        }
    }
}
