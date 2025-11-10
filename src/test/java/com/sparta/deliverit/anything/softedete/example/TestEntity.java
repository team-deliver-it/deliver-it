package com.sparta.deliverit.anything.softedete.example;

import com.deliverit.global.entity.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "p_test_entity")
@SQLDelete(sql = "UPDATE p_test_entity SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class TestEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public TestEntity() {
    }

    public Long getId() {
        return id;
    }
}
