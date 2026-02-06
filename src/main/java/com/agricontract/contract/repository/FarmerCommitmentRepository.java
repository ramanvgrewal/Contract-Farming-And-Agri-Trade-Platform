package com.agricontract.contract.repository;

import com.agricontract.contract.entity.FarmerCommitment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FarmerCommitmentRepository extends JpaRepository<FarmerCommitment, UUID> {

    List<FarmerCommitment> findByFarmerId(UUID farmerId);

    Optional<FarmerCommitment> findByContractIdAndFarmerId(UUID contractId, UUID farmerId);
}
