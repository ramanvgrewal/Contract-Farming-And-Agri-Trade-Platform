package com.agricontract.contract.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class JoinContractRequest {

    @NotNull
    @Positive
    private Double committedQuantity;
}
