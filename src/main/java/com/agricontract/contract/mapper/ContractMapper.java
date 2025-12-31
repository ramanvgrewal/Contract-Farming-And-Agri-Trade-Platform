package com.agricontract.contract.mapper;

import com.agricontract.contract.dto.BidDto;
import com.agricontract.contract.dto.ContractDto;
import com.agricontract.contract.dto.ContractFulfillmentDto;
import com.agricontract.contract.dto.CropRequirementDto;
import com.agricontract.contract.entity.Bid;
import com.agricontract.contract.entity.Contract;
import com.agricontract.contract.entity.ContractFulfillment;
import com.agricontract.contract.entity.CropRequirement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CropRequirement toRequirementEntity(CropRequirementDto dto);

    @Mapping(target = "buyerId", source = "buyer.id")
    CropRequirementDto toRequirementDto(CropRequirement entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requirement", ignore = true)
    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "farmer", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "fulfillment", ignore = true)
    Contract toContractEntity(ContractDto dto);

    @Mapping(target = "requirementId", source = "requirement.id")
    @Mapping(target = "buyerId", source = "buyer.id")
    @Mapping(target = "farmerId", source = "farmer.id")
    ContractDto toContractDto(Contract entity);

    @Mapping(target = "contractId", source = "contract.id")
    ContractFulfillmentDto toFulfillmentDto(ContractFulfillment entity);

    @Mapping(target = "contractId", source = "contract.id")
    @Mapping(target = "bidderId", source = "bidder.id")
    BidDto toBidDto(Bid entity);
}
