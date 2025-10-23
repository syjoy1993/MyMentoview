package ce2team1.mentoview.controller;

import ce2team1.mentoview.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ResponseController {
    private final ResponseService responseService;
}
