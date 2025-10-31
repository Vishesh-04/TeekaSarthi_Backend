package com.teekasarthi.teekasarthi.vaccination.service;

import com.teekasarthi.teekasarthi.vaccination.entity.VaccineStock;
import com.teekasarthi.teekasarthi.vaccination.repository.VaccineStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VaccineStockService {

    @Autowired
    private VaccineStockRepository vaccineStockRepository;

    public List<VaccineStock> findAllStock() {
        return vaccineStockRepository.findAll();
    }

    public Optional<VaccineStock> findStockById(Long id) {
        return vaccineStockRepository.findById(id);
    }

    public VaccineStock saveStock(VaccineStock vaccineStock) {
        return vaccineStockRepository.save(vaccineStock);
    }

    public void deleteStockById(Long id) {
        vaccineStockRepository.deleteById(id);
    }

    public Optional<VaccineStock> updateStockStatus(Long id, String newStatus) {
        Optional<VaccineStock> optionalStock = vaccineStockRepository.findById(id);
        if (optionalStock.isPresent()) {
            VaccineStock existingStock = optionalStock.get();
            existingStock.setStatus(newStatus);
            return Optional.of(vaccineStockRepository.save(existingStock));
        } else {
            return Optional.empty();
        }
    }

    public Optional<VaccineStock> updateStockCounts(Long id, int current, int used, int received) {
        Optional<VaccineStock> optionalStock = vaccineStockRepository.findById(id);
        if (optionalStock.isPresent()) {
            VaccineStock existingStock = optionalStock.get();
            existingStock.setCurrentStock(current);
            existingStock.setUsedStock(used);
            existingStock.setReceivedStock(received);
            return Optional.of(vaccineStockRepository.save(existingStock));
        } else {
            return Optional.empty();
        }
    }
}