package com.agricontract.pricing.snapshot.service.serviceImpl;

import com.agricontract.pricing.snapshot.dto.AIPriceSnapshot;
import com.agricontract.pricing.snapshot.entity.PriceSnapshotEntity;
import com.agricontract.pricing.snapshot.mapper.PriceSnapshotMapper;
import com.agricontract.pricing.snapshot.repository.PriceSnapshotRepository;
import com.agricontract.pricing.snapshot.service.PriceSnapshotPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PriceSnapshotPersistenceServiceImpl
        implements PriceSnapshotPersistenceService {

    private final PriceSnapshotRepository repository;
    private final PriceSnapshotMapper mapper;

    @Override
    public PriceSnapshotEntity save(AIPriceSnapshot snapshot) {
        return repository.save(mapper.toEntity(snapshot));
    }

    @Override
    public Optional<PriceSnapshotEntity> findById(UUID id) {
        return repository.findById(id);
    }
}
