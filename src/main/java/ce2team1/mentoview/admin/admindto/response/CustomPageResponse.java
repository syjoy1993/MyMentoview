package ce2team1.mentoview.admin.admindto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record CustomPageResponse<T>(
        List<T> content,
        int page,
        int size,
        int totalPages,
        long totalElements
) {
    public static <T> CustomPageResponse by(Page<T> page) {
        return new CustomPageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
