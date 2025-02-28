package ce2team1.mentoview.controller;

import ce2team1.mentoview.controller.dto.request.InterviewCreate;
import ce2team1.mentoview.controller.dto.response.InterviewDetailResp;
import ce2team1.mentoview.controller.dto.response.QuestionResp;
import ce2team1.mentoview.entity.Interview;
import ce2team1.mentoview.service.InterviewService;
import ce2team1.mentoview.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/interview/end")
    public ResponseEntity<?> createInterviewResponse(@RequestParam(name = "files") Map<Long, MultipartFile> files) {

        // 면접 답변 객체 생성 및 저장
        responseService.createResponse(files);

        return ResponseEntity.ok("");
    }

    @DeleteMapping("/interview/{interview_id}")
    public void deleteInterview(@PathVariable("interview_id") Long id) {
        interviewService.deleteInterview(id);
    }
}
