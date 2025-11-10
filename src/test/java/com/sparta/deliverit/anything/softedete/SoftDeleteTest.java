package com.sparta.deliverit.anything.softedete;

import com.deliverit.global.config.AuditingConfig;
import com.sparta.deliverit.anything.softedete.example.TestEntity;
import com.sparta.deliverit.anything.softedete.example.TestEntityRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
@EntityScan(basePackageClasses = { TestEntity.class })
@Import({AuditingConfig.class, SoftDeleteTest.TestBoot.class})
public class SoftDeleteTest {

    @Autowired
    private TestEntityRepository repository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("소프트 삭제하면 조회되지 않는다")
    void saveDeleteAndCountAlive() {
        repository.saveAll(List.of(new TestEntity(), new TestEntity()));
        em.flush();
        em.clear();

        TestEntity entity = repository.findAll().get(0);
        repository.delete(entity);
        em.flush();
        em.clear();

        int size = repository.findAll().size();
        assertEquals(1, size);
    }

    @Test
    @DisplayName("소프트 삭제 필터를 비활성화하면 삭제된 엔티티도 조회된다")
    void saveDeleteAndCountAllWithFilterOff() {
        repository.saveAll(List.of(new TestEntity(), new TestEntity()));
        em.flush();
        em.clear();

        TestEntity entity = repository.findAll().get(0);
        repository.delete(entity);
        em.flush();
        em.clear();

        em.unwrap(Session.class).disableFilter("softDeleteFilter");

        int size = repository.findAll().size();
        assertEquals(2, size);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableAspectJAutoProxy
    static class TestBoot { }
}
