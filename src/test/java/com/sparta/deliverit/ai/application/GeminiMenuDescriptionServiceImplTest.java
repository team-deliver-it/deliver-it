package com.sparta.deliverit.ai.application;

import com.deliverit.ai.application.GeminiMenuDescriptionServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.deliverit.ai.domain.entity.AiMenuDescription;
import com.deliverit.ai.domain.repository.AiMenuDescriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GeminiMenuDescriptionServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AiMenuDescriptionRepository aiMenuDescriptionRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private GeminiMenuDescriptionServiceImpl service;

    @Test
    void askQuestionToAi() {
        //given
        String prompt = "Test Prompt";
        String result = "Ai's response";
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        ObjectNode root = createJsonNodes(jsonFactory, result);

        ResponseEntity<String> response = new ResponseEntity<>(root.toString(), HttpStatus.OK);

        given(restTemplate.exchange(any(RequestEntity.class), eq(String.class))).willReturn(response);
        given(aiMenuDescriptionRepository.save(any(AiMenuDescription.class))).willReturn(AiMenuDescription.of(prompt, result));

        //when
        String aiResponse = service.askQuestionToAi(prompt);

        //then
        assertThat(aiResponse).isEqualTo(result);
    }

    private ObjectNode createJsonNodes(JsonNodeFactory jsonFactory, String result) {
        ObjectNode partsNode = jsonFactory.objectNode();
        ArrayNode partArray = jsonFactory.arrayNode();
        partsNode.put("text", result);
        partArray.add(partsNode);

        ObjectNode contentsNode = jsonFactory.objectNode();
        ArrayNode contentsArray = jsonFactory.arrayNode();
        contentsNode.set("parts", partArray);
        contentsArray.add(contentsNode);

        ObjectNode root = jsonFactory.objectNode();
        root.set("contents", contentsArray);
        return root;
    }
}