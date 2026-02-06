package com.agricontract.contract.controller;

import com.agricontract.auth.entity.User;
import com.agricontract.contract.dto.request.JoinContractRequest;
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
@RequestMapping("/api/farmer/contracts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FARMER')")
public class ContractFarmerController {

    private final ContractService contractService;

    @GetMapping("/open")
    public ResponseEntity<List<ContractResponse>> getOpenContracts() {
        return ResponseEntity.ok(contractService.getOpenContractsForFarmers());
    }

    @PostMapping("/{contractId}/join")
    public ResponseEntity<ContractResponse> joinContract(
            @PathVariable UUID contractId,
            @Valid @RequestBody JoinContractRequest request,
            Authentication authentication
    ) {
        UUID farmerId = extractUserId(authentication);
        return ResponseEntity.ok(contractService.joinContract(contractId, farmerId, request));
    }

    @GetMapping("/{contractId}")
    public ResponseEntity<ContractResponse> getContractDetails(
            @PathVariable UUID contractId
    ) {
        return ResponseEntity.ok(contractService.getContractById(contractId));
    }

    private UUID extractUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
