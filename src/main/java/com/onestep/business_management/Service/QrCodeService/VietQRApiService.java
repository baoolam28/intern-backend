package com.onestep.business_management.Service.QrCodeService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.onestep.business_management.DTO.QrTranferRequest;

@Service
public class VietQRApiService {
    private final RestTemplate restTemplate;

    @Value("${vietqr.clientId}")
    private String clientId;

    @Value("${vietqr.apiKey}")
    private String apiKey;

    public VietQRApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateQRCode(QrTranferRequest request) {
        String url = "https://api.vietqr.io/v2/generate";
        System.out.println("tranfer request: " + request.toString());
        // Tạo headers cho request
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        // Tạo request entity
        HttpEntity<QrTranferRequest> requestEntity = new HttpEntity<>(request, headers);

        // Gửi POST request đến API VietQR
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, String.class);

        

        // Kiểm tra phản hồi
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody(); // Trả về nội dung phản hồi
        } else {
            return "Error: " + responseEntity.getStatusCode() + "message: " + responseEntity.getBody(); // Trả về lỗi nếu có
        }
    }
}
