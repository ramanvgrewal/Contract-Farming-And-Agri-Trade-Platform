package com.agricontract.contract.mapper;

import com.agricontract.contract.dto.response.FarmerCommitmentResponse;
import com.agricontract.contract.entity.FarmerCommitment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FarmerCommitmentMapper {

    FarmerCommitmentResponse toResponse(FarmerCommitment commitment);
}
