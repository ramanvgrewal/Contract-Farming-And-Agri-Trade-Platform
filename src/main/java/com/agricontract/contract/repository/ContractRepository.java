package com.agricontract.contract.repository;

import com.agricontract.contract.entity.Contract;
import com.agricontract.contract.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {

    List<Contract> findByBuyerId(UUID buyerId);

    List<Contract> findByStatus(ContractStatus status);
}
