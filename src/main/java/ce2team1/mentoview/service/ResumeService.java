package ce2team1.mentoview.service;

import ce2team1.mentoview.controller.dto.response.InterviewResp;
import ce2team1.mentoview.controller.dto.response.ResumeResp;
import ce2team1.mentoview.entity.Resume;
import ce2team1.mentoview.entity.User;
import ce2team1.mentoview.repository.InterviewRepository;
import ce2team1.mentoview.repository.ResumeRepository;
import ce2team1.mentoview.repository.UserRepository;
import ce2team1.mentoview.service.dto.InterviewDto;
import ce2team1.mentoview.service.dto.ResumeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final AwsS3Service awsS3Service;
    private static final String RESUME_DIR = "resumes";

    // userId 값별로 resume list 출력
    // resumeId 값 별로 interview list 출력
    public List<ResumeResp> getResumesByUserId(Long userId) {
//        return resumeRepository.findByUserUserId(userId).stream()
//                .map(ResumeDto::from)
//                .collect(Collectors.toList());

        List<ResumeResp> resumes = resumeRepository.findByUserUserId(userId).stream()
                .map(ResumeDto::from)
                .map(ResumeResp::from)
                .toList();

        for (ResumeResp resume : resumes) {
            List<InterviewResp> interviewList = interviewRepository.findAllByResumeResumeId(resume.getResumeId())
                    .stream()
                    .map(InterviewDto::toDto)
                    .map(InterviewResp::from)
                    .toList();

            resume.updateInterviewList(interviewList);
        }

        return resumes;
    }

    // s3 bucket으로 resume Dir에서 userid Dir 별로 resume 파일 업로드
    public ResumeDto createResume(MultipartFile file, Long userId) {

        long count = resumeRepository.countAllByUserUserIdAndDeleteStatusIsFalse(userId);

        if (count >= 5) {
            throw new RuntimeException("Resume limit exceeded");
        }

        String key = RESUME_DIR + "/" + userId;
        try {
            key = awsS3Service.uploadS3(file, key);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Resume resume = Resume.of(file.getOriginalFilename(), key, user);
        resume = resumeRepository.save(resume);
        return ResumeDto.from(resume);
    }

    // s3 bucket에서 resume 삭제
    // db 정보는 title명만 변경해주는 softDelete 방식
    public void deleteResume(Long resumeId, Long userId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
        if (!resume.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this resume");
        }
        awsS3Service.deleteS3Object(resume.getS3Key());
        resume.softDelete();
        resumeRepository.save(resume);
    }
}