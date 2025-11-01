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

    /**
     * Handles both creating new stock (POST) and updating existing stock (PUT).
     * * For existing stock, this function implements the core inventory logic:
     * 1. Calculates the net change: (Received Today) - (Used Today).
     * 2. Updates the Current Stock: (Old Current Stock) + (Net Change).
     * 3. Resets 'usedStock' and 'receivedStock' to 0 for the next day's reporting.
     * * @param vaccineStock The incoming VaccineStock object from the request body.
     * @return The saved VaccineStock object.
     */
    public VaccineStock saveStock(VaccineStock vaccineStock) {
        if (vaccineStock.getId() != null) {
            Optional<VaccineStock> optionalExistingStock = vaccineStockRepository.findById(vaccineStock.getId());

            if (optionalExistingStock.isPresent()) {
                VaccineStock existingStock = optionalExistingStock.get();

                // 1. Calculate the change in stock based on data from the PUT request
                int netChange = vaccineStock.getReceivedStock() - vaccineStock.getUsedStock();

                // 2. Update current stock: Old Current Stock + Net Change
                int newCurrentStock = existingStock.getCurrentStock() + netChange;

                // --- Update fields on the existing entity ---

                // Apply the calculated current stock
                existingStock.setCurrentStock(newCurrentStock);

                // Apply non-numerical fields from the update object
                existingStock.setName(vaccineStock.getName());
                existingStock.setExpiryDate(vaccineStock.getExpiryDate());
                existingStock.setStatus(vaccineStock.getStatus());

                // 3. Reset daily tracking fields to 0 for the next GET request
                existingStock.setUsedStock(0);
                existingStock.setReceivedStock(0);

                return vaccineStockRepository.save(existingStock);
            }
        }

        // For new stocks (POST), save directly but ensure daily trackers are 0.
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

    // The previous updateStockCounts method has been removed as its logic is now contained within the unified saveStock method.
}