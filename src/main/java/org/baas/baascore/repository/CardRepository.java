package org.baas.baascore.repository;

import org.baas.baascore.model.Card;
import org.baas.baascore.model.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card,Long> {
    List<Card> findByCustomerAndCardStatusTrueAndExpiredAtGreaterThan(Customer customer, LocalDateTime date);
    Optional<Card> findByCardNumber(String cardNumber);

    @EntityGraph(attributePaths = {"customer","account"})
    Optional<Card> findByCardNumberAndCardStatusTrueAndExpiredAtGreaterThan(String cardNumber, LocalDateTime date);
}
