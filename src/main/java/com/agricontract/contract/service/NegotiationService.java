package com.agricontract.contract.service;

import com.agricontract.auth.entity.User;
import com.agricontract.common.exception.AppException;
import com.agricontract.contract.dto.BidDto;
import com.agricontract.contract.entity.Bid;
import com.agricontract.contract.entity.Contract;
import com.agricontract.contract.mapper.ContractMapper;
import com.agricontract.contract.repository.BidRepository;
import com.agricontract.contract.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NegotiationService {

    private final BidRepository bidRepository;
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Transactional
    public BidDto placeBid(UUID contractId, BidDto bidDto, User bidder) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Contract not found"));

        if (contract.getStatus() != Contract.ContractStatus.NEGOTIATING) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Contract is not in negotiation state");
        }

        long bidCount = bidRepository.countByContractId(contractId);
        if (bidCount >= 3) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Maximum 3 bids allowed per contract");
        }

        int roundNumber = (int) bidCount + 1;

        // Risk Analysis
        BigDecimal referencePrice = contract.getRequirement().getMaxPrice();
        BigDecimal deviation = bidDto.getPrice().subtract(referencePrice)
                .divide(referencePrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        String aiAnalysis = analyzeRiskWithOllama(bidDto.getPrice(), referencePrice, deviation);
        Bid.RiskLevel riskLevel = determineRiskLevel(deviation);

        Bid bid = Bid.builder()
                .contract(contract)
                .bidder(bidder)
                .roundNumber(roundNumber)
                .price(bidDto.getPrice())
                .riskLevel(riskLevel)
                .aiExplanation(aiAnalysis)
                .build();

        Bid savedBid = bidRepository.save(bid);

        if (roundNumber == 3) {
            contract.setStatus(Contract.ContractStatus.EXPIRED);
            contractRepository.save(contract);
        }

        return contractMapper.toBidDto(savedBid);
    }

    public List<BidDto> getBids(UUID contractId) {
        return bidRepository.findByContractIdOrderByRoundNumberAsc(contractId).stream()
                .map(contractMapper::toBidDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptContract(UUID contractId, User user) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Contract not found"));

        contract.setStatus(Contract.ContractStatus.ACCEPTED);
        contractRepository.save(contract);
    }

    @Transactional
    public void rejectContract(UUID contractId, User user) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Contract not found"));

        contract.setStatus(Contract.ContractStatus.REJECTED);
        contractRepository.save(contract);
    }

    private String analyzeRiskWithOllama(BigDecimal bidPrice, BigDecimal refPrice, BigDecimal deviation) {
        String prompt = String.format(
                "Analyze the risk of this agricultural contract bid. " +
                "Bid Price: %s, Market Reference Price: %s, Deviation: %s%%. " +
                "Provide a concise explanation of the risk (LOW, MEDIUM, or HIGH) and why.",
                bidPrice, refPrice, deviation
        );

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ollama", "run", "llama3", prompt);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Ollama process exited with code {}", exitCode);
                return "AI Analysis unavailable (Ollama error)";
            }

            return output.toString().trim();
        } catch (Exception e) {
            log.error("Error executing Ollama CLI", e);
            return "AI Analysis unavailable (Execution error)";
        }
    }

    private Bid.RiskLevel determineRiskLevel(BigDecimal deviation) {
        double dev = deviation.abs().doubleValue();
        if (dev < 5) return Bid.RiskLevel.LOW;
        if (dev < 15) return Bid.RiskLevel.MEDIUM;
        return Bid.RiskLevel.HIGH;
    }
}
