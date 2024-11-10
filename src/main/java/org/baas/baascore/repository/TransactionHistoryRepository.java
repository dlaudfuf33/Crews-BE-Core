package org.baas.baascore.repository;

import org.baas.baascore.model.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory,Long> {
    Optional<TransactionHistory> findByCoreTransactionIdAndAccount_FintechUseNum(Long id, String fintechNum);
}
