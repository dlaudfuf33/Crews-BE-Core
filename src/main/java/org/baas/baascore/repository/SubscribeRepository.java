package org.baas.baascore.repository;

import org.baas.baascore.model.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe,Long> {
    Optional<Subscribe> findByAccessKey(String accessKey);

    Optional<List<Subscribe>> findSubscribesByCompanyNameAndBusinessNum(String companyName, String business);

    List<Subscribe> findAllByExpireDateBefore(LocalDateTime now);
}
