package com.deliverit.restaurant.infrastructure.api.map;

import com.deliverit.global.exception.MapException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.deliverit.global.response.code.MapResponseCode.GEOCODING_API_ERROR;
import static com.deliverit.global.response.code.MapResponseCode.GEOCODING_API_TIMEOUT;

@Component
@RequiredArgsConstructor
public class KakaoMapClient {

    private final RestTemplate kakaoRestTemplate;

    /**
     * geocode 주소를 좌표로 변환하는 메서드
     *
     * @param address 주소 문자열
     * @return AddressResponseDto latitude, longitude
     * <p>
     * Kakao Map API를 호출하여 요청받은 주소 문자열을 좌표로 변환한 값으로 받아옵니다.
     * API 호출에 실패하거나 응답에 문제가 있을 경우 예외를 반환합니다.
     */
    public AddressResponseDto geocode(String address) {
        try {
            return kakaoRestTemplate.getForObject(
                    "/v2/local/search/address.json?query={query}",
                    AddressResponseDto.class,
                    address
            );
        } catch (ResourceAccessException e) {
            throw new MapException(GEOCODING_API_TIMEOUT);
        } catch (RestClientException e) {
            throw new MapException(GEOCODING_API_ERROR);
        }
    }
}
