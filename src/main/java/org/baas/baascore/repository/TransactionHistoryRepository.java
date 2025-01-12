package org.baas.baascore.repository;

import org.baas.baascore.model.Account;
import org.baas.baascore.model.TransactionHistory;
import org.baas.baascore.util.TranType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    Optional<TransactionHistory> findByCoreTransactionIdAndAccount_FintechUseNum(Long id, String fintechNum);

    @Query("""
            select t
            from TransactionHistory t
            join t.coreTransaction ct
            where t.account = :account
              and t.createdAt >= :filteredDate
              and ct.status = org.baas.baascore.util.StatusType.SUCCESS
            order by t.createdAt ASC
            """)
    @EntityGraph(attributePaths = {"card", "account"})
    List<TransactionHistory> findTransactionHistoryAllTranType(@Param("account") Account account,
                                                               @Param("filteredDate") LocalDateTime filteredDate);

    @Query("""
            select t
            from TransactionHistory t
            join t.coreTransaction ct
            where t.account = :account
              and t.createdAt >= :filteredDate
              and t.tranType = :transactionType
              and ct.status = org.baas.baascore.util.StatusType.SUCCESS
            order by t.createdAt ASC
            """)
    @EntityGraph(attributePaths = {"card", "account"})
    List<TransactionHistory> findTransactionHistorySelectedTranType(@Param("account") Account account,
                                                                    @Param("filteredDate") LocalDateTime filteredDate,
                                                                    @Param("transactionType") TranType transactionType);


    @Query("SELECT t FROM TransactionHistory t WHERE t.account =:account AND FUNCTION('YEAR', t.createdAt) = :year AND FUNCTION('MONTH', t.createdAt) = :month AND t.tranType = :transactionType ORDER BY t.createdAt DESC")
    List<TransactionHistory> findTransactionHistoryYearAndMonth(@Param("account") Account account,
                                                                @Param("year") Integer year,
                                                                @Param("month") Integer month,
                                                                @Param("transactionType") TranType transactionType);

}
