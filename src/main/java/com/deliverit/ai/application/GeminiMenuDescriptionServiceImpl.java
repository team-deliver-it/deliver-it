package com.deliverit.ai.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.deliverit.ai.domain.entity.AiMenuDescription;
import com.deliverit.ai.domain.repository.AiMenuDescriptionRepository;
import com.deliverit.global.exception.AiException;
import com.deliverit.global.response.code.AiResponseCode;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Service
@Slf4j
@Transactional
public class GeminiMenuDescriptionServiceImpl implements AiMenuDescriptionService{

    private final URI geminiURI = java.net.URI.create(
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent");
    private final AiMenuDescriptionRepository aiMenuDescriptionRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public GeminiMenuDescriptionServiceImpl(AiMenuDescriptionRepository aiMenuDescriptionRepository,
                                            ObjectMapper objectMapper,
                                            @Qualifier("geminiClient") RestTemplate restTemplate) {
        this.aiMenuDescriptionRepository = aiMenuDescriptionRepository;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public String askQuestionToAi(String question) {

        GeminiRequestDto geminiRequestDto = GeminiRequestDto.of(question);

        RequestEntity<GeminiRequestDto> requestEntity =
                new RequestEntity<>(geminiRequestDto, HttpMethod.POST, geminiURI);

        ResponseEntity<String> QuestionResult = restTemplate.exchange(requestEntity, String.class);

        String result = parseJsonToString(QuestionResult);
        aiMenuDescriptionRepository.save(AiMenuDescription.of(question, result));

        return result;
    }

    private String parseJsonToString(ResponseEntity<String> QuestionResult) {

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(QuestionResult.getBody());
        } catch (JsonProcessingException e) {
            throw new AiException(AiResponseCode.INTERNAL_SERVER_ERROR);
        }

        JsonNode resultBody = jsonNode.findValue("text");
        String result = resultBody.asText();

        if(StringUtils.hasText(result))
            return result;
        else throw new AiException(AiResponseCode.INTERNAL_SERVER_ERROR);
    }

    @Getter
    @AllArgsConstructor
    public static class GeminiRequestDto{

        private List<Content> contents;

        public static GeminiRequestDto of(String text) {
            List<Content> contentList = List.of(
                    new Content(
                            List.of(new Part(text))
                    ));
            return new GeminiRequestDto(contentList);
        }

        @Getter
        @AllArgsConstructor
        public static class Content{
            private List<Part> parts;

        }
        @Getter
        @AllArgsConstructor
        public static class Part{
            private String text;
        }
    }
}
