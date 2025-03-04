package ce2team1.mentoview.controller;

import ce2team1.mentoview.controller.dto.request.InterviewCreate;
import ce2team1.mentoview.controller.dto.request.ResponseUpdate;
import ce2team1.mentoview.controller.dto.response.InterviewDetailResp;
import ce2team1.mentoview.controller.dto.response.QuestionResp;
import ce2team1.mentoview.service.InterviewService;
import ce2team1.mentoview.service.ResponseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class InterviewController {

    private final InterviewService interviewService;
    private final ResponseService responseService;

    // 개별 면접 조회 (면접의 FAQ 정보 조회)
    @GetMapping("/interview/{interview_id}")
    public List<InterviewDetailResp> getInterviewById(@PathVariable("interview_id") Long id) {
        return interviewService.getInterviewDetailByInterviewId(id)
                .stream()
                .map(dto ->
                        InterviewDetailResp.builder()
                                .questionId(dto.getQuestionId())
                                .question(dto.getQuestion())
                                .answer(dto.getAnswer())
                                .feedback(dto.getFeedback())
                                .build())
                .toList();
    }

    // Polling 으로 인터뷰 상태 조회
    @GetMapping("/interview/{interview_id}/status")
    public ResponseEntity<String> getInterviewStatus(@PathVariable("interview_id") Long id) {
        String status = interviewService.getInterviewStatus(id);
        return ResponseEntity.ok(status);
    }

    // 면접 시작 -> 면접 생성 후 질문 생성
    @PostMapping("/interview/start")
    public List<QuestionResp> createInterviewAndQuestion(@RequestBody InterviewCreate interviewCreate) {
        return interviewService.createInterviewQuestion(interviewCreate)
                .stream()
                .map(dto ->
                        QuestionResp.builder()
                                .questionId(dto.getQuestionId())
                                .interviewId(dto.getInterviewId())
                                .question(dto.getQuestion())
                                .build())
                .toList();
    }

    // 면접 종료
    // 프론트 API 연동 시 파라미터 다시 수정!
    @PostMapping("/interview/end")
    public ResponseEntity<?> createInterviewResponse(
//            @RequestParam(name = "files") Map<Long, MultipartFile> files
            @RequestPart("fileData") String fileData,
            @RequestPart("files") List<MultipartFile> files) throws JsonProcessingException {

        // JSON 데이터를 Map으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        Map<Long, String> fileMapping = objectMapper.readValue(fileData, new TypeReference<>() {});
        Map<Long, MultipartFile> fileMap = new HashMap<>();

        // 파일과 매핑된 ID 출력 및 파라미터 변환
        for (int i = 0; i < files.size(); i++) {
            long qid = Long.parseLong(fileMapping.get((long) i + 1));
            MultipartFile voiceFile = files.get(i);

            System.out.println("ID: " + qid + ", File: " + voiceFile.getOriginalFilename());

            fileMap.put(qid, voiceFile);
        }

        // 면접 답변 객체 생성 및 저장
//        responseService.createResponse(files);
        responseService.createResponse(fileMap);

        return ResponseEntity.ok().build();
    }

    // 면접 응답 업데이트
    @PostMapping("/interview/response/transcription")
    public ResponseEntity<Void> updateTranscription(@RequestBody ResponseUpdate responseUpdate) {

        responseService.updateTranscriptionAndGenerateFeedback(responseUpdate);
        return ResponseEntity.ok().build();
    }

    // 면접 삭제
    @DeleteMapping("/interview/{interview_id}")
    public void deleteInterview(@PathVariable("interview_id") Long id) {
        interviewService.deleteInterview(id);
    }
}
