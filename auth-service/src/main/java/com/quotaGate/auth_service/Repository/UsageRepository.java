package com.quotaGate.auth_service.Repository;

import com.quotaGate.auth_service.Domain.Usage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsageRepository extends JpaRepository<Usage, Long> {
    Optional<Usage> findByUser_Id(Long userId);
}
