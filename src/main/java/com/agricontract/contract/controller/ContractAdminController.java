package com.agricontract.contract.controller;

import com.agricontract.contract.dto.response.ContractResponse;
import com.agricontract.contract.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/contracts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ContractAdminController {

    private final ContractService contractService;

    @GetMapping("/{contractId}")
    public ResponseEntity<ContractResponse> getContract(
            @PathVariable UUID contractId
    ) {
        return ResponseEntity.ok(contractService.getContractById(contractId));
    }

    // Optional: later add filters (status, buyer, etc.)
}
