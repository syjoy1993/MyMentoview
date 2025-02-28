package ce2team1.mentoview.service;


import ce2team1.mentoview.entity.InterviewQuestion;
import ce2team1.mentoview.entity.InterviewResponse;
import ce2team1.mentoview.repository.InterviewQuestionRepository;
import ce2team1.mentoview.repository.InterviewResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ResponseService {

    private final InterviewResponseRepository responseRepository;
    private final InterviewQuestionRepository questionRepository;

    // 미완성 메서드입니다.
    public void createResponse(Map<Long, MultipartFile> files) {
        List<InterviewQuestion> questionList = questionRepository.findAllByQuestionIdIn(files.keySet());

        for (InterviewQuestion question : questionList) {
            MultipartFile file = files.get(question.getQuestionId());

            // s3에 file 업로드하고 url 반환
            String fileUrl = "url";
            InterviewResponse response = InterviewResponse.of(fileUrl,
                    null,
                    true,
                    Duration.ofSeconds(60),
                    question);

            // response 저장
            responseRepository.save(response);
        }
    }
}
