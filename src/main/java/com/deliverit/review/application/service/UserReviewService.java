package com.deliverit.review.application.service;

import com.deliverit.global.exception.UserException;
import com.deliverit.global.response.code.UserResponseCode;
import com.deliverit.review.application.service.dto.OrderReviewInfo;
import com.deliverit.review.domain.entity.OrderReview;
import com.deliverit.review.infrastructure.repository.OrderReviewRepository;
import com.deliverit.user.domain.entity.User;
import com.deliverit.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserReviewService {
    private final UserRepository userRepository;
    private final OrderReviewRepository orderReviewRepository;

    @Transactional(readOnly = true)
    public List<OrderReviewInfo> getUserReviews(Long userId) {
        User user = getUserByUserId(userId);
        // FIXME: 페이지네이션 적용
        List<OrderReview> orderReviews = orderReviewRepository.findAllByUserIdWithUser(user.getId());
        return OrderReviewInfo.fromList(orderReviews);
    }

    private User getUserByUserId(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 유저입니다. id : {}", userId);
                    return new UserException(UserResponseCode.NOT_FOUND_USER);
                });

        return user;
    }
}
