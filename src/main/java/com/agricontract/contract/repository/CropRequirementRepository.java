package com.agricontract.contract.repository;

import com.agricontract.contract.entity.CropRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CropRequirementRepository extends JpaRepository<CropRequirement, UUID> {
}
