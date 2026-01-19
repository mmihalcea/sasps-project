package edu.saspsproject.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;


@Slf4j
@Component
public class ApiSMSAdapter implements SmsProvider{
    @Override
    public boolean sendSms(String phoneNumber, String message) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("Authorization", "API_KEY");

        String jsonTemplate = "{"
                + "\"messages\":[{"
                + "\"destinations\":[{\"to\":\"%s\"}],"
                + "\"from\":\"447491163443\","
                + "\"text\":\"%s\""
                + "}]"
                + "}";

        String jsonBody = String.format(jsonTemplate, "+4" + phoneNumber, message);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        String url = "https://api.infobip.com/sms/2/text/advanced";

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        System.out.println("Response: " + response.getBody());
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public boolean supports(String phoneNumber) {
        return true;
    }

    @Override
    public String getProviderName() {
        return "API - infobip";
    }
}
