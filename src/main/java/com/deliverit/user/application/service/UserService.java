package com.deliverit.user.application.service;


import com.deliverit.global.exception.UserException;
import com.deliverit.global.response.code.UserResponseCode;
import com.deliverit.user.application.service.dto.UserInfo;
import com.deliverit.user.domain.entity.User;
import com.deliverit.user.domain.entity.UserRoleEnum;
import com.deliverit.user.domain.repository.UserRepository;
import com.deliverit.user.presentation.dto.SignupRequestDto;
import com.deliverit.user.presentation.dto.UserEditRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.deliverit.global.response.code.UserResponseCode.UNAUTHORIZED_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";
    private static final Map<String, UserRoleEnum> ROLE_ALIASES = buildRoleAliases();

    public void signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("ID 중복");
        }

        String phone = requestDto.getPhone();
        Optional<User> checkPhone = userRepository.findByPhone(phone);
        if (checkPhone.isPresent()) {
            throw new IllegalArgumentException("전화번호 중복.");
        }

        UserRoleEnum requestedRole = resolveRole(requestDto.getRole());

        // 역할별 검증/분기
        // 권한별 추가 로직이 생기면 추가예정
        switch (requestedRole) {
            case MASTER, MANAGER -> {
                if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                    throw new IllegalArgumentException("관리자 토큰 불일치");
                }
            }
            case OWNER -> {

            }
            case CUSTOMER -> {

            }
        }

        String name = requestDto.getName();

        User user = new User(username, password, name , phone, requestedRole);
        userRepository.save(user);
    }

    public UserInfo getUserInfo(Long userId) {
        User user = getUserById(userId);

        return new UserInfo(
                user.getName(),
                user.getPhone(),
                user.getRole().name()
        );
    }

    public Long deleteUser(Long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
        return userId;
    }

    private static Map<String, UserRoleEnum> buildRoleAliases() {
        Map<String, UserRoleEnum> roleMap = new HashMap<>();
        for (UserRoleEnum r : UserRoleEnum.values()) {
            roleMap.put(r.name(), r);
            roleMap.put(r.getAuthority(), r);
        }
        return roleMap;
    }

    private static String normalize(String s) {
        return s == null ? null : s.trim().toUpperCase(Locale.ROOT);
    }

    private static UserRoleEnum resolveRole(String input) {
        String key = normalize(input);
        if (key == null || key.isEmpty()) return UserRoleEnum.CUSTOMER; // 기본값
        UserRoleEnum r = ROLE_ALIASES.get(key);
        if (r == null) {
            throw new IllegalArgumentException("지원하지 않는 권한입니다: " + input);
        }
        return r;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("존재하지 않는 유저입니다. userId : {}", userId);
            return new UserException(UserResponseCode.NOT_FOUND_USER);
        });
    }

    public void editUser(Long userId, UserEditRequestDto requestDto) {
        User user = getUserById(userId);

        if (!user.getId().equals(userId)) {
            log.error("[UserService.editUser] 인증되지 않은 유저 접근입니다. userId : {}", userId);

            throw new UserException(UNAUTHORIZED_USER);
        }

        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            log.error("[UserService.editUser] 유저 정보 변경 중 비밀번호가 일치하지 않습니다.");

            throw new UserException(UserResponseCode.PASSWORD_MISMATCH);
        }

        user.changePassword(passwordEncoder.encode(requestDto.getNewPassword()));

        user.updateProfile(requestDto);

        userRepository.save(user);
    }
}
