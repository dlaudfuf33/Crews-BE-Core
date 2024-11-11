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

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory,Long> {

    @Query("select t from TransactionHistory t where t.account =:account and t.createdAt >= :filteredDate order by t.createdAt ASC")
    @EntityGraph(attributePaths = {"card", "account"})
    List<TransactionHistory> findTransactionHistoryAllTranType(@Param("account") Account account,
                                                               @Param("filteredDate") LocalDateTime filteredDate);

    @Query("select t from TransactionHistory t where t.account =:account and t.createdAt >= :filteredDate and t.tranType = :transactionType order by t.createdAt ASC")
    @EntityGraph(attributePaths = {"card", "account"})
    List<TransactionHistory> findTransactionHistorySelectedTranType(@Param("account") Account account,
                                                                    @Param("filteredDate") LocalDateTime filteredDate,
                                                                    @Param("transactionType") TranType transactionType);

}
