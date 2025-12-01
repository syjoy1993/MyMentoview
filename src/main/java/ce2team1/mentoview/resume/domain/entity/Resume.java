package ce2team1.mentoview.resume.domain.entity;


import ce2team1.mentoview.user.domain.entity.User;
import ce2team1.mentoview.user.domain.entity.atrribute.AuditingFields;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Resume extends AuditingFields {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_id")
    private Long resumeId;

    private String title; // User의 실제 파일명 -> Resume 삭제시 : "삭제된 이력서 입니다." 로 변경
    @Column(name = "s3_key")
    private String s3Key; // 이력서 삭제 시 연관된 s3 오브젝트 삭제
    @Column(name = "delete_status")
    private boolean deleteStatus; // resume 삭제 여부 판별 true : 삭제, false 미삭제

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Resume of(String title, String s3Key, User user) {
        return new Resume (null,title, s3Key, false, user);

    }

    public static Resume of(Long resumeId ,String title, String s3Key, User user) {
        return new Resume (resumeId, title, s3Key, false, user);
    }

    public void softDelete() {
        this.title = "This is a deleted resume.";
        this.deleteStatus = true;
        this.s3Key = null;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Resume resume = (Resume) o;
        return getResumeId() != null && Objects.equals(getResumeId(), resume.getResumeId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
