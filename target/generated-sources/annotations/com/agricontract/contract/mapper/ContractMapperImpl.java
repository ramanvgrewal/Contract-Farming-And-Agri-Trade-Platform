package com.agricontract.contract.mapper;

import com.agricontract.contract.dto.request.CreateContractRequest;
import com.agricontract.contract.dto.response.ContractResponse;
import com.agricontract.contract.dto.response.FarmerCommitmentResponse;
import com.agricontract.contract.entity.Contract;
import com.agricontract.contract.entity.FarmerCommitment;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-24T11:49:50+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Amazon.com Inc.)"
)
@Component
public class ContractMapperImpl extends ContractMapper {

    @Autowired
    private FarmerCommitmentMapper farmerCommitmentMapper;

    @Override
    public Contract toEntity(CreateContractRequest request, UUID buyerId) {
        if ( request == null ) {
            return null;
        }

        Contract.ContractBuilder contract = Contract.builder();

        contract.cropName( request.getCropName() );
        contract.state( request.getState() );
        contract.cropVariety( request.getCropVariety() );
        contract.requiredQuantity( request.getRequiredQuantity() );
        contract.priceType( request.getPriceType() );
        contract.offeredPriceMin( request.getOfferedPriceMin() );
        contract.offeredPriceMax( request.getOfferedPriceMax() );
        if ( request.getLocationRequired() != null ) {
            contract.locationRequired( request.getLocationRequired() );
        }
        contract.locationLat( request.getLocationLat() );
        contract.locationLng( request.getLocationLng() );
        contract.locationRadiusKm( request.getLocationRadiusKm() );
        if ( request.getClusterEnabled() != null ) {
            contract.clusterEnabled( request.getClusterEnabled() );
        }

        contract.buyerId( buyerId );
        contract.filledQuantity( (double) 0.0 );

        return contract.build();
    }

    @Override
    public ContractResponse toResponse(Contract contract) {
        if ( contract == null ) {
            return null;
        }

        ContractResponse.ContractResponseBuilder contractResponse = ContractResponse.builder();

        contractResponse.priceSnapshot( mapSnapshot( contract.getPriceSnapshotId() ) );
        contractResponse.id( contract.getId() );
        contractResponse.buyerId( contract.getBuyerId() );
        contractResponse.cropName( contract.getCropName() );
        contractResponse.cropVariety( contract.getCropVariety() );
        contractResponse.requiredQuantity( contract.getRequiredQuantity() );
        contractResponse.filledQuantity( contract.getFilledQuantity() );
        contractResponse.priceType( contract.getPriceType() );
        contractResponse.offeredPriceMin( contract.getOfferedPriceMin() );
        contractResponse.offeredPriceMax( contract.getOfferedPriceMax() );
        contractResponse.actualStartDate( contract.getActualStartDate() );
        contractResponse.actualEndDate( contract.getActualEndDate() );
        contractResponse.locationRequired( contract.isLocationRequired() );
        contractResponse.locationLat( contract.getLocationLat() );
        contractResponse.locationLng( contract.getLocationLng() );
        contractResponse.locationRadiusKm( contract.getLocationRadiusKm() );
        contractResponse.clusterEnabled( contract.isClusterEnabled() );
        contractResponse.status( contract.getStatus() );
        contractResponse.createdAt( contract.getCreatedAt() );
        contractResponse.commitments( farmerCommitmentListToFarmerCommitmentResponseList( contract.getCommitments() ) );

        return contractResponse.build();
    }

    protected List<FarmerCommitmentResponse> farmerCommitmentListToFarmerCommitmentResponseList(List<FarmerCommitment> list) {
        if ( list == null ) {
            return null;
        }

        List<FarmerCommitmentResponse> list1 = new ArrayList<FarmerCommitmentResponse>( list.size() );
        for ( FarmerCommitment farmerCommitment : list ) {
            list1.add( farmerCommitmentMapper.toResponse( farmerCommitment ) );
        }

        return list1;
    }
}
