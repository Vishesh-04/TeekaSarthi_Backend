package com.teekasarthi.teekasarthi.vaccination.service;

import com.teekasarthi.teekasarthi.supervisor.entity.Center;
import com.teekasarthi.teekasarthi.supervisor.repository.CenterRepository;
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

    @Autowired
    private CenterRepository centerRepository;

    // --- NEW: Find stock specific to a center ---
    public List<VaccineStock> findStockByCenterId(Long centerId) {
        return vaccineStockRepository.findByCenterId(centerId);
    }

    // --- NEW: Add stock linked to a center ---
    public VaccineStock addStockToCenter(Long centerId, VaccineStock vaccineStock) {
        // 1. Fetch the Center entity
        Center center = centerRepository.findById(centerId)
                .orElseThrow(() -> new RuntimeException("Center not found with ID: " + centerId));

        // 2. Link the stock to the center
        vaccineStock.setCenter(center);

        // 3. Initialize daily trackers to 0
        vaccineStock.setUsedStock(0);
        vaccineStock.setReceivedStock(0);

        return vaccineStockRepository.save(vaccineStock);
    }

    // Existing methods below...

    public List<VaccineStock> findAllStock() {
        return vaccineStockRepository.findAll();
    }

    public Optional<VaccineStock> findStockById(Long id) {
        return vaccineStockRepository.findById(id);
    }

    /**
     * Handles both creating new stock (POST) and updating existing stock (PUT).
     */
    public VaccineStock saveStock(VaccineStock vaccineStock) {
        if (vaccineStock.getId() != null) {
            Optional<VaccineStock> optionalExistingStock = vaccineStockRepository.findById(vaccineStock.getId());

            if (optionalExistingStock.isPresent()) {
                VaccineStock existingStock = optionalExistingStock.get();

                // 1. Calculate the change in stock
                int netChange = vaccineStock.getReceivedStock() - vaccineStock.getUsedStock();

                // 2. Update current stock
                int newCurrentStock = existingStock.getCurrentStock() + netChange;

                // --- Update fields on the existing entity ---
                existingStock.setCurrentStock(newCurrentStock);
                existingStock.setName(vaccineStock.getName());
                existingStock.setExpiryDate(vaccineStock.getExpiryDate());
                existingStock.setStatus(vaccineStock.getStatus());

                // Ensure Center is preserved if passed, or keep existing
                if (vaccineStock.getCenter() != null) {
                    existingStock.setCenter(vaccineStock.getCenter());
                }

                // 3. Reset daily tracking fields
                existingStock.setUsedStock(0);
                existingStock.setReceivedStock(0);

                return vaccineStockRepository.save(existingStock);
            }
        }

        // For new stocks not linked via addStockToCenter (fallback)
        vaccineStock.setUsedStock(0);
        vaccineStock.setReceivedStock(0);
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
}