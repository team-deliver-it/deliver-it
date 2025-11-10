package com.deliverit.restaurant.infrastructure.api.map;

import com.deliverit.global.exception.MapException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import static com.deliverit.global.response.code.MapResponseCode.ADDRESS_GEOCODING_FAILED;
import static com.deliverit.global.response.code.MapResponseCode.INVALID_ADDRESS_INPUT;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapService {

    private final KakaoMapClient kakaoMapClient;

    /**
     * geocode 주소를 좌표로 변환하는 메서드
     *
     * @param address 주소 문자열
     * @return Coordinates latitude, longitude
     * <p>
     * Kakao Map API에 주소 문자열을 보내 좌표 값을 요청합니다.
     * 주소가 비어있거나 좌표로 변환할 수 없는 경우 예외를 반환합니다.
     */
    public Coordinates geocode(String address) {
        if (!StringUtils.hasText(address))
            throw new MapException(INVALID_ADDRESS_INPUT);

        AddressResponseDto response = kakaoMapClient.geocode(address);

        if (response == null || CollectionUtils.isEmpty(response.getDocuments())) {
            throw new MapException(ADDRESS_GEOCODING_FAILED);
        }

        AddressResponseDto.Document doc = response.getDocuments().get(0);
        Coordinates coordinates = new Coordinates(doc.getLongitude(), doc.getLatitude());

        log.debug("주소 -> 좌표 변환: lon={}, lat={}", coordinates.getLongitude(), coordinates.getLatitude());
        return coordinates;
    }
}