package com.teekasarthi.teekasarthi.vaccination.repository;

import com.teekasarthi.teekasarthi.vaccination.entity.VaccineStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VaccineStockRepository extends JpaRepository<VaccineStock, Long> {
     Optional<VaccineStock> findByName(String name);
}
