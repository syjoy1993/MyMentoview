package ce2team1.mentoview.interview.presentation;

import ce2team1.mentoview.exception.ErrorResult;
import ce2team1.mentoview.exception.InterviewException;
import ce2team1.mentoview.interview.application.orchestrator.InterviewOrchestrator;
import ce2team1.mentoview.interview.application.orchestrator.ResponseOrchestrator;
import ce2team1.mentoview.interview.application.service.InterviewService;
import ce2team1.mentoview.interview.presentation.dto.request.InterviewCreate;
import ce2team1.mentoview.interview.presentation.dto.request.ResponseUpdate;
import ce2team1.mentoview.interview.presentation.dto.response.InterviewDetailResp;
import ce2team1.mentoview.interview.presentation.dto.response.QuestionResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final InterviewOrchestrator interviewOrchestrator;
    private final ResponseOrchestrator responseOrchestrator;

    // 개별 면접 조회 (면접의 FAQ 정보 조회)
    @Operation(summary = "면접 FAQ 조회", description = "완료 상태인 특정 면접의 질문, 답변, 피드백 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "면접 FAQ 정보가 정상적으로 반환되었습니다."),
            @ApiResponse(responseCode = "403", description = "면접이 완료되지 않았습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResult.class)
                    )),
            @ApiResponse(responseCode = "404", description = "잘못된 면접 ID로 요청하였거나, 해당 면접 정보가 존재하지 않습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResult.class)
                    ))
    })
    @GetMapping("/interview/{interview_id}")
    public ResponseEntity<List<InterviewDetailResp>> getInterviewById(@PathVariable("interview_id") Long id) {
        List<InterviewDetailResp> resp = interviewService.getInterviewDetailByInterviewId(id)
                .stream()
                .map(dto ->
                        InterviewDetailResp.builder()
                                .questionId(dto.getQuestionId())
                                .question(dto.getQuestion())
                                .answer(dto.getAnswer())
                                .feedback(dto.getFeedback())
                                .build())
                .toList();

        return ResponseEntity.ok(resp);
    }

    // Polling 으로 인터뷰 상태 조회
    @Operation(summary = "면접 상태 조회", description = "특정 면접의 진행 상태를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "면접 상태가 정상적으로 반환되었습니다."),
            @ApiResponse(responseCode = "404", description = "잘못된 면접 ID로 요청하였습니다.")
    })
    @GetMapping("/interview/{interview_id}/status")
    public ResponseEntity<String> getInterviewStatus(@PathVariable("interview_id") Long id) {
        String status = interviewService.getInterviewStatus(id);
        return ResponseEntity.ok(status);
    }

    // 면접 시작 -> 면접 생성 후 질문 생성
    @Operation(summary = "면접 시작", description = "새로운 면접을 생성하고, 자동으로 면접 질문을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "면접이 성공적으로 생성되었으며, 질문 목록이 반환됩니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "InvalidRequestData", description = "요청 데이터가 올바르지 않습니다.",
                                            value = "{\"message\": \"Invalid request data\", \"code\": 400}"),
                                    @ExampleObject(name = "InvalidFileType", description = "이력서 파일 확장자가 PDF가 아닙니다.",
                                            value = "{\"message\": \"이력서 파일은 PDF 형식만 가능합니다.\", \"code\": 400}")
                            })),
            @ApiResponse(responseCode = "404", description = "잘못된 면접 ID로 요청하였거나, 해당 면접 정보가 존재하지 않습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResult.class)
                    )),
            @ApiResponse(responseCode = "422", description = "이력서에서 텍스트를 추출할 수 없습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResult.class)
                    )),
            @ApiResponse(responseCode = "500", description = "서버에 문제가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResult.class)
                    ))
    })
    @PostMapping("/interview/start")
    public ResponseEntity<List<QuestionResp>> createInterviewAndQuestion(@RequestBody InterviewCreate interviewCreate) {
        List<QuestionResp> resp = interviewOrchestrator.startInterview(interviewCreate)
                .stream()
                .map(dto ->
                        QuestionResp.builder()
                                .questionId(dto.getQuestionId())
                                .interviewId(dto.getInterviewId())
                                .question(dto.getQuestion())
                                .build())
                .toList();

        return ResponseEntity.ok(resp);
    }

    // 면접 종료
    // 프론트 API 연동 시 파라미터 다시 수정!
    @Operation(summary = "면접 종료", description = "면접을 종료하고, 질문별 음성 파일을 업로드하여 답변을 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "면접 종료 후 답변이 성공적으로 저장되었습니다."),
            @ApiResponse(responseCode = "400", description = "파일 데이터가 올바르게 전달되지 않았거나, 요청 형식이 잘못되었습니다."),
            @ApiResponse(responseCode = "500", description = "면접 응답 업로드 중 서버 문제가 발생했습니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResult.class)
                    ))
    })
    @PostMapping("/interview/end")
    public ResponseEntity<?> createInterviewResponse(@RequestPart("files") List<MultipartFile> files) {

        // JSON 데이터를 Map으로 변환
        Map<Long, MultipartFile> fileMap = new HashMap<>();

        // 파일과 매핑된 ID를 출력하며 처리
        for (MultipartFile file : files) {
            // 파일명에서 questionId 추출
            String filename = file.getOriginalFilename();
            if (filename != null && filename.startsWith("audio_")) {
                long questionId = Long.parseLong(filename.substring(6, filename.lastIndexOf('.')));

                fileMap.put(questionId, file);
            } else {
                throw new InterviewException("잘못된 파일명 - " + filename, HttpStatus.BAD_REQUEST);
            }
        }

        // 면접 답변 객체 생성 및 저장
        responseOrchestrator.processInterviewResponse(fileMap);

        return ResponseEntity.ok().build();
    }

    // 면접 응답 업데이트
    @PostMapping("/interview/response/transcription")
    @Operation(summary = "면접 응답 업데이트", description = "AWS Transcribe를 통해 변환된 텍스트를 저장하고, AI 피드백을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "면접 응답이 정상적으로 업데이트되고 피드백이 생성되었습니다."),
            @ApiResponse(responseCode = "400", description = "요청 데이터가 올바르지 않거나, 면접 정보가 존재하지 않습니다.")
    })
    public ResponseEntity<Void> updateTranscription(@RequestBody ResponseUpdate responseUpdate) {
        responseOrchestrator.handleTranscriptionUpdate(responseUpdate);
        return ResponseEntity.ok().build();
    }

    // 면접 삭제
    @Operation(summary = "면접 삭제", description = "특정 면접을 삭제합니다. 관련된 질문과 답변 데이터도 함께 삭제됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "면접이 정상적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "404", description = "해당 면접 ID가 존재하지 않거나 삭제할 수 없는 상태입니다.")
    })
    @DeleteMapping("/interview/{interview_id}")
    public ResponseEntity<Void> deleteInterview(@PathVariable("interview_id") Long id) {
        interviewService.deleteInterview(id);
        return ResponseEntity.ok().build();
    }
}
