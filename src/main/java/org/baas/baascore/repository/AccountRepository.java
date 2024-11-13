package org.baas.baascore.repository;

import jakarta.persistence.LockModeType;
import org.baas.baascore.model.Account;
import org.baas.baascore.model.Customer;
import org.baas.baascore.util.AccountType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @EntityGraph(attributePaths = {"customer", "bank"})

    // 단순 조회 (락 없이)
    Optional<Account> findByAccountNumber(String accountNumber);

    @EntityGraph(attributePaths = {"bank"})
    Optional<Account> findByFintechUseNum(String fintechUseNum);

    List<Account> findByCustomerAndIsDeletedAndAccountType(Customer customer, boolean isDeleted, AccountType accountType);

    @Override
    @EntityGraph(attributePaths = {"customer", "bank"})
    Account save(Account account);

    @EntityGraph(attributePaths = {"customer", "bank"})
    List<Account> findByCustomerId(Long customerId);

    // 수정 작업을 위한 조회 (JPQL + 락 적용)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    @EntityGraph(attributePaths = {"customer", "bank"})
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    @Query("SELECT a FROM Account a WHERE a.fintechUseNum = :fintechUseNum")
    @EntityGraph(attributePaths = {"customer", "bank"})
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByFintechUseNumForUpdate(@Param("fintechUseNum") String fintechUseNum);


}
