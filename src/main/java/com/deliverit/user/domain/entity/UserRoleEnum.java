package com.deliverit.user.domain.entity;

public enum UserRoleEnum {
    CUSTOMER(Authority.CUSTOMER),  // 고객
    OWNER(Authority.OWNER), // 사장님
    MANAGER(Authority.MANAGER),  // 관리자 매니저
    MASTER(Authority.MASTER);  // 전체 관리자 계정

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String CUSTOMER = "ROLE_CUSTOMER";
        public static final String OWNER = "ROLE_OWNER";
        public static final String MANAGER = "ROLE_MANAGER";
        public static final String MASTER = "ROLE_MASTER";
    }
}