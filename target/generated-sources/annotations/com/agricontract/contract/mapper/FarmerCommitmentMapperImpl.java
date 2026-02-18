package com.agricontract.contract.mapper;

import com.agricontract.contract.dto.response.FarmerCommitmentResponse;
import com.agricontract.contract.entity.FarmerCommitment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-14T17:22:09+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Amazon.com Inc.)"
)
@Component
public class FarmerCommitmentMapperImpl implements FarmerCommitmentMapper {

    @Override
    public FarmerCommitmentResponse toResponse(FarmerCommitment commitment) {
        if ( commitment == null ) {
            return null;
        }

        FarmerCommitmentResponse.FarmerCommitmentResponseBuilder farmerCommitmentResponse = FarmerCommitmentResponse.builder();

        farmerCommitmentResponse.id( commitment.getId() );
        farmerCommitmentResponse.farmerId( commitment.getFarmerId() );
        farmerCommitmentResponse.committedQuantity( commitment.getCommittedQuantity() );
        farmerCommitmentResponse.status( commitment.getStatus() );
        farmerCommitmentResponse.createdAt( commitment.getCreatedAt() );

        return farmerCommitmentResponse.build();
    }
}
