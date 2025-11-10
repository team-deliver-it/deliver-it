package com.deliverit.global.persistence;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class HibernateFilterAspect {
    private final EntityManager em;

    @Around("@within(com.deliverit.global.persistence.UseActiveRestaurantFilter) || @annotation(com.deliverit.global.persistence.UseActiveRestaurantFilter)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        var session = em.unwrap(org.hibernate.Session.class);
        var filter = session.enableFilter("activeRestaurantFilter");
        try {
            return pjp.proceed();
        } finally {
            session.disableFilter("activeRestaurantFilter");
        }
    }
}