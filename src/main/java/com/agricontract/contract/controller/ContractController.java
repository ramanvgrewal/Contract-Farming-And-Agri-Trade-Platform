package com.agricontract.contract.controller;

import com.agricontract.auth.entity.User;
import com.agricontract.contract.dto.BidDto;
import com.agricontract.contract.dto.ContractDto;
import com.agricontract.contract.dto.CropRequirementDto;
import com.agricontract.contract.service.ContractService;
import com.agricontract.contract.service.NegotiationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final NegotiationService negotiationService;

    @PostMapping("/requirements")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<CropRequirementDto> createRequirement(
            @Valid @RequestBody CropRequirementDto dto,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(contractService.createRequirement(dto, user));
    }

    @GetMapping("/requirements")
    public ResponseEntity<List<CropRequirementDto>> getAllRequirements() {
        return ResponseEntity.ok(contractService.getAllRequirements());
    }

    @PostMapping
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ContractDto> createContract(
            @Valid @RequestBody ContractDto dto,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(contractService.createContract(dto, user));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ContractDto>> getMyContracts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(contractService.getMyContracts(user));
    }

    @PostMapping("/{id}/bids")
    public ResponseEntity<BidDto> placeBid(
            @PathVariable UUID id,
            @Valid @RequestBody BidDto bidDto,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(negotiationService.placeBid(id, bidDto, user));
    }

    @GetMapping("/{id}/bids")
    public ResponseEntity<List<BidDto>> getBids(@PathVariable UUID id) {
        return ResponseEntity.ok(negotiationService.getBids(id));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptContract(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        negotiationService.acceptContract(id, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectContract(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        negotiationService.rejectContract(id, user);
        return ResponseEntity.ok().build();
    }
}
