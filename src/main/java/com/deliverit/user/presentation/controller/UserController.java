package com.deliverit.user.presentation.controller;

import com.deliverit.global.infrastructure.security.UserDetailsImpl;
import com.deliverit.global.response.ApiResponse;
import com.deliverit.user.application.service.UserService;
import com.deliverit.user.application.service.dto.UserInfo;
import com.deliverit.user.presentation.dto.SignupRequestDto;
import com.deliverit.user.presentation.dto.UserEditRequestDto;
import com.deliverit.user.presentation.dto.UserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.deliverit.global.response.code.UserResponseCode.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/v1")
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/auth/register",
            consumes = "application/json",
            produces = "text/plain; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        userService.signup(requestDto);

        return ResponseEntity.ok(requestDto.getName() + "님 가입을 환영합니다.");
    }

    @GetMapping("/users/profile")
    public ApiResponse<UserResponseDto> getUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getId();
        log.info("=== 회원 조회 userId : {} ===", userId);
        UserInfo userInfo = userService.getUserInfo(userId);
        log.info("=== 회원 조회 완료 ===");
        return ApiResponse.create(
                USER_QUERY_SUCCESS,
                USER_QUERY_SUCCESS.getMessage(),
                UserResponseDto.from(userInfo)
        );
    }

    @DeleteMapping("/users")
    public ApiResponse<Long> deleteUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getId();
        log.info("=== 회원 삭제 userId : {} ===", userId);
        Long deletedUserId = userService.deleteUser(userId);
        log.info("=== 회원 삭제 완료 ===");
        return ApiResponse.create(
                USER_DELETE_SUCCESS,
                USER_DELETE_SUCCESS.getMessage(),
                deletedUserId
        );
    }

    @PutMapping("/users")
    public ApiResponse<Void> editUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserEditRequestDto userEditRequest
    ) {
        userService.editUser(userDetails.getId(), userEditRequest);

        return ApiResponse.create(
                USER_EDIT_SUCCESS,
                USER_EDIT_SUCCESS.getMessage(),
                null
        );
    }
}
