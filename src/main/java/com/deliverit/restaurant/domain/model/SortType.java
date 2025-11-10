package com.deliverit.restaurant.domain.model;

public enum SortType {
    CREATED_AT("createdAt"),
    DISTANCE("distance"),
    RATING("rating"),
    ;

    private final String field;

    SortType(String field) {
        this.field = field;
    }

    public String field() {
        return field;
    }

    public static SortType normalize(String requested) {
        if (requested == null) return CREATED_AT; // default: sort=createAt
        try {
            return SortType.valueOf(requested.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CREATED_AT;
        }
    }
}