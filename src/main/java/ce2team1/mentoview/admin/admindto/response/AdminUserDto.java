package ce2team1.mentoview.admin.admindto.response;

import ce2team1.mentoview.entity.atrribute.Role;
import ce2team1.mentoview.entity.atrribute.UserStatus;

import java.time.LocalDate;

public record AdminUserDto(String userId,
                           String email,
                           Role role,
                           LocalDate joinedDate, // create at
                           UserStatus status
                           //AdminSubDto subscription
                           ) {
}
