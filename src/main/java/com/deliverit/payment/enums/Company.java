package com.deliverit.payment.enums;

import com.deliverit.global.exception.PaymentException;
import com.deliverit.global.response.code.PaymentResponseCode;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Company {
    SAMSUNG("삼성"), KB("KB");

    @Getter
    private final String name;

    private static final Map<String, Company> companies = Stream.of(values())
            .collect(Collectors.toMap(Company::getName, c -> c));

    Company(String name) {
        this.name = name;
    }

    public static Company of(String name) {
        if(!companies.containsKey(name)) throw new PaymentException(PaymentResponseCode.INVALID_COMPANY);
        return companies.get(name);
    }
}
