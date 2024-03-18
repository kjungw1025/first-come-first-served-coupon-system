package com.project.couponcore.service;

import com.project.couponcore.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.stream.IntStream;

import static com.project.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static org.junit.jupiter.api.Assertions.*;

class CouponIssueRedisServiceTest extends TestConfig {

    @Autowired
    CouponIssueRedisService sut;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void clear() {
        // Redis에 이전에 테스트하고 남은 데이터가 다음 테스트에 영향을 주지 않도록
        Collection<String> redisKeys = redisTemplate.keys("*");
        redisTemplate.delete(redisKeys);
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 발급 가능 수량이 존재하면 true를 반환한다.")
    void availableTotalIssueQuantity_1() {
        // given
        int totalIssueQuantity = 10;
        long couponId = 1;

        // when
        boolean result = sut.availableTotalIssueQuantity(totalIssueQuantity, couponId);

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("쿠폰 수량 검증 - 발급 가능 수량이 모두 소진되면 false를 반환한다.")
    void availableTotalIssueQuantity_2() {
        // given
        int totalIssueQuantity = 10;
        long couponId = 1;
        IntStream.range(0, totalIssueQuantity).forEach(userId -> {
            redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));
        });

        // when
        boolean result = sut.availableTotalIssueQuantity(totalIssueQuantity, couponId);

        // then
        Assertions.assertFalse(result);
    }
}