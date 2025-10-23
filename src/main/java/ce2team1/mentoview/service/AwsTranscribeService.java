package ce2team1.mentoview.service;

import ce2team1.mentoview.service.dto.ResponseTranscribeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.LanguageCode;
import software.amazon.awssdk.services.transcribe.model.Media;
import software.amazon.awssdk.services.transcribe.model.StartTranscriptionJobRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AwsTranscribeService {

    //Upload 하고자 하는 버킷의 이름
    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;
    private final TranscribeClient transcribeClient;

    private static final String TRANSCRIBE_DIR = "transcribe/";

    public void transcribeInterviewResponse(List<ResponseTranscribeDto> transcribeList) {
        for (ResponseTranscribeDto dto : transcribeList) {
            transcribeProcess(dto);
        }
    }

    private void transcribeProcess(ResponseTranscribeDto response) {
        String jobName = "transcription-interview-" + response.getResponseId();
        String s3Key = response.getFileUrl();

        // 파일 이름에서 확장자 추출
        System.out.println(s3Key);

        String fileName = s3Key.substring(s3Key.lastIndexOf("/") + 1);
        String mediaFormat = determineMediaFormat(fileName); // 동적으로 mediaFormat 설정

        String s3Uri = "s3://" + bucketName + "/" + s3Key;

        StartTranscriptionJobRequest jobRequest = StartTranscriptionJobRequest.builder()
                .transcriptionJobName(jobName)
                .media(Media.builder().mediaFileUri(s3Uri).build())
                .mediaFormat(mediaFormat) // mp3, wav, flac 등 가능
                .identifyLanguage(true) // 자동 감지
                .languageOptions(LanguageCode.KO_KR, LanguageCode.EN_US) // 한국어 & 영어 감지만 허용
                .outputBucketName(bucketName) // 변환된 텍스트가 저장될 S3 버킷
                .outputKey(TRANSCRIBE_DIR)
                .build();

        transcribeClient.startTranscriptionJob(jobRequest);
    }

    // 미디어 포맷 확장자 검증
    private String determineMediaFormat(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        return switch (extension) {
            case "mp3" -> "mp3";
            case "mp4" -> "mp4";
            case "wav" -> "wav";
            case "flac" -> "flac";
            case "webm" -> "webm";
            case "m4a" -> "m4a";
            default -> throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + extension);
        };
    }
}
