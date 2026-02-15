package com.agricontract.contract.mapper;

import com.agricontract.contract.dto.request.CreateContractRequest;
import com.agricontract.contract.dto.response.ContractResponse;
import com.agricontract.contract.entity.Contract;
import com.agricontract.pricing.snapshot.mapper.PriceSnapshotMapper;
import com.agricontract.pricing.snapshot.repository.PriceSnapshotRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = FarmerCommitmentMapper.class)
public abstract class ContractMapper {

    @Autowired
    protected PriceSnapshotRepository priceSnapshotRepository;

    @Autowired
    protected PriceSnapshotMapper priceSnapshotMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "buyerId", expression = "java(buyerId)")
    @Mapping(target = "filledQuantity", constant = "0.0")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "commitments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "actualStartDate", ignore = true)
    @Mapping(target = "actualEndDate", ignore = true)
    public abstract Contract toEntity(CreateContractRequest request, @Context UUID buyerId);

    @Mapping(target = "priceSnapshot", source = "priceSnapshotId", qualifiedByName = "mapSnapshot")
    public abstract ContractResponse toResponse(Contract contract);

    @Named("mapSnapshot")
    protected com.agricontract.contract.dto.response.PriceSnapshotResponse mapSnapshot(UUID snapshotId) {
        if (snapshotId == null) return null;
        return priceSnapshotRepository.findById(snapshotId)
                .map(priceSnapshotMapper::toResponse)
                .orElse(null);
    }
}
