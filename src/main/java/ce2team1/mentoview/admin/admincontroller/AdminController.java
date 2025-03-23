package ce2team1.mentoview.admin.admincontroller;

import ce2team1.mentoview.admin.admindto.request.AdminUserSearchCond;
import ce2team1.mentoview.admin.admindto.response.AdminUserDto;
import ce2team1.mentoview.admin.adminservice.AdminUserService;
import lombok.Getter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUserService adminUserService;

    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserDto>> searchUser(@Validated @RequestBody AdminUserSearchCond cond, Pageable pageable) {
        Page<AdminUserDto> result = adminUserService.search(cond, pageable);
        return ResponseEntity.ok(result);
    }

}
