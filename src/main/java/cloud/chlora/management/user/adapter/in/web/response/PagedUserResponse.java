package cloud.chlora.management.user.adapter.in.web.response;

import java.util.List;

public record PagedUserResponse<T>(
        long totalElements,
        int page,
        int size,
        int totalPages,
        List<T> content
) {}