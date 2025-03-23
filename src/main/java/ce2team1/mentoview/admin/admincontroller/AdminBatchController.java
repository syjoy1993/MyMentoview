package ce2team1.mentoview.admin.admincontroller;


import ce2team1.mentoview.archive.service.ArchiveBatch;
import ce2team1.mentoview.security.service.RefreshTokenBatchJob;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/batch")
@RequiredArgsConstructor
public class AdminBatchController {
    private final RefreshTokenBatchJob tokenBatchJob;
    private final ArchiveBatch archiveBatch;

    @PostMapping("/refresh-token")
    public ResponseEntity<String> runRefreshTokenBatch() {
        tokenBatchJob.deleteExpiredTokens();
        return ResponseEntity.ok("리프레시 토큰 정리 배치가 수동 실행되었습니다.");
    }

    @PostMapping("/user-archive")
    public ResponseEntity<String> runUserArchiveBatch() {
        archiveBatch.archiveDeletedUsers();
        return ResponseEntity.ok("유저 아카이브 배치가 수동 실행되었습니다.");
    }

}
