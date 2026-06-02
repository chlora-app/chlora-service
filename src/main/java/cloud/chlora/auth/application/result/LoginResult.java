package cloud.chlora.auth.application.result;

import cloud.chlora.auth.adapter.in.web.response.LoginResponse;
import cloud.chlora.auth.common.response.BaseResponse;

public record LoginResult(
        BaseResponse<LoginResponse> response,
        String token
) {}