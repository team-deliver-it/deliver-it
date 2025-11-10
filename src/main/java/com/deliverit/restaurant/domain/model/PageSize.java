package com.deliverit.restaurant.domain.model;

public enum PageSize {
    TEN(10),
    THIRTY(30),
    FIFTY(50),
    ;

    private final int value;

    PageSize(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static int normalize(Integer requested) {
        if (requested == null) return TEN.value; // default: size=10

        for (PageSize s : values()) {
            if (s.value == requested) return s.value;
        }

        return TEN.value; // 10, 30, 50 외의 숫자는 10으로 변환
    }
}