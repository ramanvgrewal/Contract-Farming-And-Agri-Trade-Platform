package com.agricontract.contract.service;

import com.agricontract.auth.entity.User;
import com.agricontract.common.exception.AppException;
import com.agricontract.contract.dto.ContractDto;
import com.agricontract.contract.dto.CropRequirementDto;
import com.agricontract.contract.entity.Contract;
import com.agricontract.contract.entity.ContractFulfillment;
import com.agricontract.contract.entity.CropRequirement;
import com.agricontract.contract.mapper.ContractMapper;
import com.agricontract.contract.repository.ContractRepository;
import com.agricontract.contract.repository.CropRequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final CropRequirementRepository requirementRepository;
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Transactional
    public CropRequirementDto createRequirement(CropRequirementDto dto, User buyer) {
        CropRequirement requirement = contractMapper.toRequirementEntity(dto);
        requirement.setBuyer(buyer);
        requirement.setStatus(CropRequirement.RequirementStatus.OPEN);
        return contractMapper.toRequirementDto(requirementRepository.save(requirement));
    }

    public List<CropRequirementDto> getAllRequirements() {
        return requirementRepository.findAll().stream()
                .map(contractMapper::toRequirementDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContractDto createContract(ContractDto dto, User farmer) {
        CropRequirement requirement = requirementRepository.findById(dto.getRequirementId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Requirement not found"));

        if (requirement.getStatus() != CropRequirement.RequirementStatus.OPEN) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Requirement is no longer open");
        }

        Contract contract = contractMapper.toContractEntity(dto);
        contract.setRequirement(requirement);
        contract.setBuyer(requirement.getBuyer());
        contract.setFarmer(farmer);
        contract.setStatus(Contract.ContractStatus.SIGNED);
        requirement.setStatus(CropRequirement.RequirementStatus.FULFILLED);

        ContractFulfillment fulfillment = ContractFulfillment.builder()
                .contract(contract)
                .harvestedQuantity(0.0)
                .deliveredQuantity(0.0)
                .build();
        contract.setFulfillment(fulfillment);

        return contractMapper.toContractDto(contractRepository.save(contract));
    }

    public List<ContractDto> getMyContracts(User user) {
        // In a real microservices scenario, this might be filtered by DB query
        return contractRepository.findAll().stream()
                .filter(c -> c.getBuyer().getId().equals(user.getId()) || c.getFarmer().getId().equals(user.getId()))
                .map(contractMapper::toContractDto)
                .collect(Collectors.toList());
    }
}
