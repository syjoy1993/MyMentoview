package ce2team1.mentoview.admin.adminservice;

import ce2team1.mentoview.admin.admindto.request.AdminUserSearchCond;
import ce2team1.mentoview.admin.admindto.response.AdminUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminUserService {
    public Page<AdminUserDto> search(AdminUserSearchCond cond, Pageable pageable) {


        return null;
    }
}
