package com.agricontract.contract.service.serviceImpl;

import com.agricontract.common.exception.AppException;
import com.agricontract.contract.dto.request.CreateContractRequest;
import com.agricontract.contract.dto.request.JoinContractRequest;
import com.agricontract.contract.dto.response.ContractResponse;
import com.agricontract.contract.entity.Contract;
import com.agricontract.contract.entity.FarmerCommitment;
import com.agricontract.contract.enums.CommitmentStatus;
import com.agricontract.contract.enums.ContractStatus;
import com.agricontract.contract.mapper.ContractMapper;
import com.agricontract.contract.repository.ContractRepository;
import com.agricontract.contract.repository.FarmerCommitmentRepository;
import com.agricontract.contract.service.ContractService;
import com.agricontract.pricing.snapshot.repository.PriceSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final FarmerCommitmentRepository commitmentRepository;
    private final ContractMapper contractMapper;
    private final PriceSnapshotRepository priceSnapshotRepository;

    // ---------------- BUYER ----------------

    @Override
    public ContractResponse createContract(CreateContractRequest request, UUID buyerId) {

        priceSnapshotRepository.findById(request.getPriceSnapshotId())
                .orElseThrow(() -> new AppException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid price snapshot"
                ));


        validateContractRequest(request);

        Contract contract = contractMapper.toEntity(request, buyerId);
        contract.setStatus(ContractStatus.CREATED);

        Contract saved = contractRepository.save(contract);
        return contractMapper.toResponse(saved);
    }

    @Override
    public ContractResponse openContract(UUID contractId, UUID buyerId) {

        Contract contract = getOwnedContract(contractId, buyerId);

        if (contract.getStatus() != ContractStatus.CREATED) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Contract cannot be opened in current state");
        }

        contract.setStatus(ContractStatus.OPEN_FOR_ACCEPTANCE);
        return contractMapper.toResponse(contract);
    }

    @Override
    public List<ContractResponse> getContractsForBuyer(UUID buyerId) {
        return contractRepository.findByBuyerId(buyerId)
                .stream()
                .map(contractMapper::toResponse)
                .toList();
    }

    // ---------------- FARMER ----------------

    @Override
    public ContractResponse joinContract(UUID contractId, UUID farmerId, JoinContractRequest request) {

        Contract contract = getOpenContract(contractId);

        if (!contract.isClusterEnabled() && contract.getFilledQuantity() > 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Contract does not allow multiple farmers");
        }

        commitmentRepository.findByContractIdAndFarmerId(contractId, farmerId)
                .ifPresent(c -> {
                    throw new AppException(HttpStatus.BAD_REQUEST, "Farmer already joined this contract");
                });

        double remainingQty = contract.getRequiredQuantity() - contract.getFilledQuantity();
        if (request.getCommittedQuantity() > remainingQty) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Committed quantity exceeds remaining quantity");
        }

        FarmerCommitment commitment = FarmerCommitment.builder()
                .contract(contract)
                .farmerId(farmerId)
                .committedQuantity(request.getCommittedQuantity())
                .status(CommitmentStatus.ACCEPTED)
                .build();

        commitmentRepository.save(commitment);

        contract.setFilledQuantity(contract.getFilledQuantity() + request.getCommittedQuantity());

        // Locking logic
        if (contract.getFilledQuantity().equals(contract.getRequiredQuantity())) {
            contract.setStatus(ContractStatus.LOCKED);
        } else {
            contract.setStatus(ContractStatus.PARTIALLY_FILLED);
        }

        return contractMapper.toResponse(contract);
    }

    @Override
    public List<ContractResponse> getOpenContractsForFarmers() {
        return contractRepository.findByStatus(ContractStatus.OPEN_FOR_ACCEPTANCE)
                .stream()
                .map(contractMapper::toResponse)
                .toList();
    }

    // ---------------- COMMON ----------------

    @Override
    public ContractResponse getContractById(UUID contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Contract not found"));
        return contractMapper.toResponse(contract);
    }

    // ---------------- VALIDATIONS ----------------

    private Contract getOwnedContract(UUID contractId, UUID buyerId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Contract not found"));

        if (!contract.getBuyerId().equals(buyerId)) {
            throw new AppException(HttpStatus.FORBIDDEN, "You do not own this contract");
        }

        return contract;
    }

    private Contract getOpenContract(UUID contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Contract not found"));

        if (contract.getStatus() != ContractStatus.OPEN_FOR_ACCEPTANCE &&
                contract.getStatus() != ContractStatus.PARTIALLY_FILLED) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Contract is not open for joining");
        }

        return contract;
    }

    private void validateContractRequest(CreateContractRequest request) {

        if (request.getContractEndDate().isBefore(request.getContractStartDate())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Contract end date must be after start date");
        }

        if (request.getPriceType().name().equals("RANGE")) {
            if (request.getOfferedPriceMin() == null || request.getOfferedPriceMax() == null) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Price range must be provided");
            }
        }

        if (Boolean.TRUE.equals(request.getLocationRequired())) {
            if (request.getLocationLat() == null ||
                    request.getLocationLng() == null ||
                    request.getLocationRadiusKm() == null) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Location details required when location is enabled");
            }
        }
    }
}
