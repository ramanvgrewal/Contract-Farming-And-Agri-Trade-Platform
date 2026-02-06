package com.agricontract.contract.controller;

import com.agricontract.auth.entity.User;
import com.agricontract.contract.dto.request.CreateContractRequest;
import com.agricontract.contract.dto.response.ContractResponse;
import com.agricontract.contract.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/buyer/contracts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class ContractBuyerController {

    private final ContractService contractService;

    @PostMapping
    public ResponseEntity<ContractResponse> createContract(
            @Valid @RequestBody CreateContractRequest request,
            Authentication authentication
    ) {
        UUID buyerId = extractUserId(authentication);
        return ResponseEntity.ok(contractService.createContract(request, buyerId));
    }

    @PostMapping("/{contractId}/open")
    public ResponseEntity<ContractResponse> openContract(
            @PathVariable UUID contractId,
            Authentication authentication
    ) {
        UUID buyerId = extractUserId(authentication);
        return ResponseEntity.ok(contractService.openContract(contractId, buyerId));
    }

    @GetMapping
    public ResponseEntity<List<ContractResponse>> getMyContracts(
            Authentication authentication
    ) {
        UUID buyerId = extractUserId(authentication);
        return ResponseEntity.ok(contractService.getContractsForBuyer(buyerId));
    }

    @GetMapping("/{contractId}")
    public ResponseEntity<ContractResponse> getContract(
            @PathVariable UUID contractId
    ) {
        return ResponseEntity.ok(contractService.getContractById(contractId));
    }

    private UUID extractUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
