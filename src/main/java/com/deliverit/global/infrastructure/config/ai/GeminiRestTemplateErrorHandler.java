package com.deliverit.global.infrastructure.config.ai;

import com.deliverit.global.exception.AiException;
import com.deliverit.global.response.code.AiResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

@Component
@Slf4j
public class GeminiRestTemplateErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response)  {
        HttpStatusCode statusCode;
        try {
            statusCode = response.getStatusCode();
        } catch (IOException e) {
            log.error("GeminiRestTemplateErrorHandler - response.getStatusCode() throw");
            throw new AiException(AiResponseCode.INTERNAL_SERVER_ERROR);
        }

        if (statusCode.is4xxClientError()) {
            log.error("GeminiRestTemplateErrorHandler - StatusCode is 4xx");
            throw new AiException(AiResponseCode.INPUT_DATA_ERROR);
        } else if (statusCode.is5xxServerError()) {
            log.error("GeminiRestTemplateErrorHandler - StatusCode is 5xx");
            throw new AiException(AiResponseCode.AI_SERVER_ERROR);
        }
    }
}
