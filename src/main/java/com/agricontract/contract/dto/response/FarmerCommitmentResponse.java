package com.agricontract.contract.dto.response;

import com.agricontract.contract.enums.CommitmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class FarmerCommitmentResponse {

    private UUID id;
    private UUID farmerId;
    private Double committedQuantity;
    private CommitmentStatus status;
    private Instant createdAt;
}
