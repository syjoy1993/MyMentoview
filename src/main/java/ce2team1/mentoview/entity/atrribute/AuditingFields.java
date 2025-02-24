package ce2team1.mentoview.entity.atrribute;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class AuditingFields {
    @Column(name = "created_at")
    @CreatedDate
    protected LocalDateTime createdAt ;


    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false, length = 20)
    protected String createdBy;

    @LastModifiedDate
    @Column(name = "modified_at")
    protected LocalDateTime modifiedAt;

    @LastModifiedBy
    @Column(name = "modified_by",nullable = false, updatable = true, length = 20)
    protected String modifiedBy;
}
