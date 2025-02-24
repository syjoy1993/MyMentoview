package ce2team1.mentoview.entity;

import ce2team1.mentoview.entity.atrribute.AuditingFields;
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

    private String title; // User의 실제 파일명 -> Resume 삭제시 : "User삭제한 파일 입니다." 로 변경
    private String fileUrl; // s3 경로 : 삭제시 날리기

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Resume of(String title, String fileUrl, User user) {
        return new Resume (null,title, fileUrl, user);

    }
    public static Resume of(Long resumeId ,String title, String fileUrl, User user) {
        return new Resume (resumeId,title, fileUrl, user);

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
