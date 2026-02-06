package com.agricontract.contract.mapper;

import com.agricontract.contract.dto.request.CreateContractRequest;
import com.agricontract.contract.dto.response.ContractResponse;
import com.agricontract.contract.entity.Contract;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = FarmerCommitmentMapper.class)
public interface ContractMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "buyerId", source = "buyerId")
    @Mapping(target = "filledQuantity", constant = "0.0")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "commitments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Contract toEntity(CreateContractRequest request, @Context UUID buyerId);

    ContractResponse toResponse(Contract contract);
}
