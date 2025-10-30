package ce2team1.mentoview.archive.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Entity
@ToString
public class InterviewArchive  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //복합 UNIQUE: user_id, interview_key
    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="interview_key", nullable=false, length=512)
    private String interviewKey; // S3 키 값

    @Column(nullable = false)
    private LocalDateTime archivedAt; // 아카이브된 시간


    public static InterviewArchive of(Long userId, String interviewKey) {
        return InterviewArchive.builder()
                .userId(userId)
                .interviewKey(interviewKey)
                .archivedAt(LocalDateTime.now())
                .build();
    }
}



