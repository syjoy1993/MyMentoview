package ce2team1.mentoview.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.transcribe.TranscribeClient;

@Configuration
public class AwsConfig {

    @Value("${cloud.aws.credentials.access-key}")
    private String awsAccessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String awsSecretKey;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    @Bean
    // access key, secret key를 사용하여 AwsBasicCredentials의 새 인스턴스 생성
    public AwsCredentials basicAWSCredentials() {
        return AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
    }

    @Bean
    public S3Client s3Client(AwsCredentials awsCredentials) {
        return S3Client.builder()
                .region(Region.of(awsRegion)) //AWS 리전 설정
                // 파라미터로 전달된 awsCredentials를 사용하여 자격증명공급자 설정
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    @Bean
    public TranscribeClient transcribeClient(AwsCredentials awsCredentials) {
        return TranscribeClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}