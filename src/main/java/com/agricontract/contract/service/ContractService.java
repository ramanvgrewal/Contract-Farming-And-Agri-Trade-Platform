package com.agricontract.contract.service;

import com.agricontract.contract.dto.request.CreateContractRequest;
import com.agricontract.contract.dto.request.JoinContractRequest;
import com.agricontract.contract.dto.response.ContractResponse;

import java.util.List;
import java.util.UUID;

public interface ContractService {

    ContractResponse createContract(CreateContractRequest request, UUID buyerId);

    ContractResponse openContract(UUID contractId, UUID buyerId);

    ContractResponse joinContract(UUID contractId, UUID farmerId, JoinContractRequest request);

    List<ContractResponse> getContractsForBuyer(UUID buyerId);

    List<ContractResponse> getOpenContractsForFarmers();

    ContractResponse getContractById(UUID contractId);
}
