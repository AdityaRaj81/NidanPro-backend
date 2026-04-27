package nidanpro_backend.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import nidanpro_backend.dto.GenerateReceiptRequest;
import nidanpro_backend.dto.ReceiptResponse;
import nidanpro_backend.service.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReceiptController {

  private final ReceiptService receiptService;

  @PostMapping("/generate")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN','SAMPLE_COLLECTOR')")
  public ResponseEntity<ReceiptResponse> generate(
      @Valid @RequestBody GenerateReceiptRequest request,
      Principal principal) {
    return ResponseEntity.ok(receiptService.generateReceipt(request, principal.getName()));
  }
}
