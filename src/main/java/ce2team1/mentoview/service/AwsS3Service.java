package ce2team1.mentoview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {

    private final S3Client s3Client;

    //Upload 하고자 하는 버킷의 이름
    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;

    // 외부 호출 S3 업로드 메소드
    public String uploadS3(MultipartFile multipartFile, String targetDir) throws IOException {
        return this.putS3(multipartFile, targetDir + "/" + multipartFile.getOriginalFilename());
    }

    // 파일 업로드를 하기위한 PutObjectRequest 반환
    private PutObjectRequest getPutObjectRequest(String key) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
    }

    // MultipartFile을 업로드 하기 위해 RequestBody.frominputStream에 InputStream과 file의 size를 입력
    private RequestBody getFileRequestBody(MultipartFile file) throws IOException {
        return RequestBody.fromInputStream(file.getInputStream(), file.getSize());
    }

    // S3Utilities를 통해 GetUrlRequest를 파라미터로 넣어 파라미터로 넘어온 key의 접근 경로를 URL로 반환받아 경로를 사용
    private String findUploadKeyUrl (String key) {
        S3Utilities s3Utilities = s3Client.utilities();
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        URL url = s3Utilities.getUrl(request);

        return url.toString();
    }

    // S3로 파일 업로드 메소드
    private String putS3(MultipartFile file, String key) throws IOException {
        PutObjectRequest objectRequest = getPutObjectRequest(key);
        RequestBody rb = getFileRequestBody(file);
        s3Client.putObject(objectRequest, rb);

        return findUploadKeyUrl(key);
    }

    // 외부 호출 S3 다운로드 메소드
    public File downloadS3ToLocal(String key, String localDir) throws IOException {
        ResponseInputStream<GetObjectResponse> s3ObjectResponseInputStream = getS3ObjectInputStream(key);
        String fileName = new File(key).getName();
        File localFile = new File(localDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(localFile)) {
            byte[] readBuffer = new byte[1024];
            int readLength;
            while ((readLength = s3ObjectResponseInputStream.read(readBuffer)) > 0) {
                fos.write(readBuffer, 0, readLength);
            }
        }
        return localFile;
    }

    // s3 파일 InputStream 으로 불러오는 메소드
    public ResponseInputStream<GetObjectResponse> getS3ObjectInputStream(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

    // 외부 호출 s3 삭제 메소드
    public void deleteS3Object(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }
}