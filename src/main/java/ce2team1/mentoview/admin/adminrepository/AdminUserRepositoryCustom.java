package ce2team1.mentoview.admin.adminrepository;

import ce2team1.mentoview.admin.admindto.request.AdminUserSearchCond;
import ce2team1.mentoview.admin.admindto.response.AdminUserDto;
import ce2team1.mentoview.service.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserRepositoryCustom {
    Page<AdminUserDto> search(AdminUserSearchCond cond, Pageable pageable);
}
