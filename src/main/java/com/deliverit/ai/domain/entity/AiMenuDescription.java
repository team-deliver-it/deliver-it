package com.deliverit.ai.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Table(name = "p_ai_menu_description")
@SQLDelete(sql = "UPDATE p_payment SET deleted_at = CURRENT_TIMESTAMP WHERE payment_id = ?")
@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AiMenuDescription {

    @Id
    @Column(name = "ai_menu_description_id")
    private String id;

    private String question;

    private String response;

    public static AiMenuDescription of(String question, String response) {
        return AiMenuDescription.builder()
                .id(UUID.randomUUID().toString().substring(0, 24))
                .question(question)
                .response(response)
                .build();
    }
}
