package com.onestep.business_management.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onestep.business_management.DTO.QrTranferRequest;
import com.onestep.business_management.Service.QrCodeService.VietQRApiService;

@RestController
@RequestMapping("/api/qr")
public class QrCodeController {
    @Autowired
    private VietQRApiService vietQrApiService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateQRCode(@RequestBody QrTranferRequest qrTransferRequest) {
        // Gọi service để tạo mã QR
        String result = vietQrApiService.generateQRCode(qrTransferRequest);

        return ResponseEntity.ok(result);
    }
}
