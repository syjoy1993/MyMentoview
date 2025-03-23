package ce2team1.mentoview.security.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RefreshTokenBatchJobTest {

    @Autowired
    private RefreshTokenBatchJob batchJob;

    @Test
    void deleteExpiredTokens() {
        batchJob.deleteExpiredTokens();
    }

    @Test
    void rotateRefreshToken() {
    }
}