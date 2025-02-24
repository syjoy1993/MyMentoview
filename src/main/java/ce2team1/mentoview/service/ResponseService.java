package ce2team1.mentoview.service;


import ce2team1.mentoview.repository.InterviewResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResponseService {
    private final InterviewResponseRepository responseRepository;
}
