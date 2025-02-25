package ce2team1.mentoview.controller;

import ce2team1.mentoview.controller.dto.request.UserRead;
import ce2team1.mentoview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/test")
    public String test (UserRead userRead) {
        return "ok";
    }
}
