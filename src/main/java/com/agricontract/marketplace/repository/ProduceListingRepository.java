package com.agricontract.marketplace.repository;

import com.agricontract.marketplace.entity.ProduceListing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProduceListingRepository extends JpaRepository<ProduceListing, UUID> {
}
