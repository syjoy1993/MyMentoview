package ce2team1.mentoview.entity;


import ce2team1.mentoview.entity.atrribute.AuditingFields;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.Duration;
import java.util.Objects;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class InterviewResponse extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long responseId;

    private String respUrl;// 음성파일 저장 url

    @Lob
    @Column(columnDefinition = "TEXT")
    private String response; // 위스퍼가 변환
    private Boolean answered; // 상태 필드 3가 필요시 Enum
    private Duration duration;//?

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    private InterviewQuestion question;

    public static InterviewResponse of(String respUrl, String response,Boolean answered, Duration duration,InterviewQuestion question) {
        return new InterviewResponse (null, respUrl, response, answered, duration, question);
    }
    public static InterviewResponse of(Long responseId,String respUrl, String response,Boolean answered, Duration duration,InterviewQuestion question) {
        return new InterviewResponse (responseId, respUrl, response,answered, duration,question );
    }








    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        InterviewResponse that = (InterviewResponse) o;
        return getResponseId() != null && Objects.equals(getResponseId(), that.getResponseId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
