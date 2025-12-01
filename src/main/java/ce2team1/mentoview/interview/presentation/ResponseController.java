package ce2team1.mentoview.interview.presentation;

import ce2team1.mentoview.interview.application.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ResponseController {
    private final ResponseService responseService;
}
