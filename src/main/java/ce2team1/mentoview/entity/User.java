package ce2team1.mentoview.entity;


import ce2team1.mentoview.entity.atrribute.AuditingFields;
import ce2team1.mentoview.entity.atrribute.Role;
import ce2team1.mentoview.entity.atrribute.SocialProvider;
import ce2team1.mentoview.entity.atrribute.UserStatus;
import ce2team1.mentoview.service.dto.UserDto;
import ce2team1.mentoview.utils.jpaconverter.UserRoleConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email; // OAuth + 폼

    @Column(nullable = true)
    private String password;//폼전용

    private String name;

    @Convert(converter = UserRoleConverter.class)
    private Role role;

    @Column(nullable = true)
    private SocialProvider socialProvider;// OAuth

    @Column(nullable = true)
    private String socialId;// OAuth

    private boolean isSocial;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private String refreshToken;

    public static User of(String email, String password, String name, Role role, SocialProvider socialProvider, String socialId, boolean isSocial, UserStatus status, String refreshToken ) {
        SocialProvider finalSocialProvider = socialProvider != null ? socialProvider : SocialProvider.NONE;
        String finalSocialId = (finalSocialProvider == SocialProvider.NONE) ? null : socialId;
        return new User(
                null, email, password, name, role != null ? role : Role.USER, finalSocialProvider, finalSocialId, false, status != null ? status : UserStatus.ACTIVE, null
        );

    }
    public static User toEntity(UserDto userDto) {
        return User.builder()
                .userId(userDto.getUserId())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .name(userDto.getName())
                .role(userDto.getRole())
                .socialProvider(userDto.getSocialProvider())
                .socialId(userDto.getSocialId())
                .isSocial(userDto.isSocial())
                .status(userDto.getStatus())
                .build();

    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getUserId() != null && Objects.equals(getUserId(), user.getUserId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
