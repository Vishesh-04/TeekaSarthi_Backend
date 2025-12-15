package com.teekasarthi.teekasarthi.vaccination.controller;

import com.teekasarthi.teekasarthi.vaccination.entity.VaccineStock;
import com.teekasarthi.teekasarthi.vaccination.service.VaccineStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/stock")
public class VaccineStockController {

    @Autowired
    private VaccineStockService vaccineStockService;

    // 1. GET ALL STOCK FOR A SPECIFIC CENTER
    // Endpoint: GET /api/stock/center/{centerId}
    @GetMapping("/center/{centerId}")
    public ResponseEntity<List<VaccineStock>> getStockByCenter(@PathVariable Long centerId) {
        List<VaccineStock> stocks = vaccineStockService.findStockByCenterId(centerId);
        if (stocks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(stocks);
    }

    // 2. GET SINGLE STOCK ITEM BY STOCK ID (Unchanged, ID is unique)
    // Endpoint: GET /api/stock/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VaccineStock> getStockById(@PathVariable Long id) {
        Optional<VaccineStock> stock = vaccineStockService.findStockById(id);
        return stock.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 3. CREATE STOCK LINKED TO A SPECIFIC CENTER
    // Endpoint: POST /api/stock/center/{centerId}
    @PostMapping("/center/{centerId}")
    public ResponseEntity<VaccineStock> addStockToCenter(@PathVariable Long centerId, @RequestBody VaccineStock vaccineStock) {
        // You must update your Service to handle the logic of finding the Center
        // by ID and setting it to the vaccineStock object before saving.
        VaccineStock savedStock = vaccineStockService.addStockToCenter(centerId, vaccineStock);
        return new ResponseEntity<>(savedStock, HttpStatus.CREATED);
    }

    // 4. UPDATE STOCK (Unchanged, updates specific row by ID)
    // Endpoint: PUT /api/stock/{id}
    @PutMapping("/{id}")
    public ResponseEntity<VaccineStock> updateStock(@PathVariable Long id, @RequestBody VaccineStock updatedStock) {
        return vaccineStockService.findStockById(id)
                .map(existingStock -> {
                    updatedStock.setId(id);
                    // Ensure the existing center association is preserved if not passed in body
                    updatedStock.setCenter(existingStock.getCenter());

                    VaccineStock savedStock = vaccineStockService.saveStock(updatedStock);
                    return ResponseEntity.ok(savedStock);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 5. UPDATE STATUS (Unchanged)
    // Endpoint: PATCH /api/stock/{id}/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<VaccineStock> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        if (newStatus == null) {
            return ResponseEntity.badRequest().build();
        }
        return vaccineStockService.updateStockStatus(id, newStatus)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 6. DELETE STOCK
    // Endpoint: DELETE /api/stock/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        if (vaccineStockService.findStockById(id).isPresent()) {
            vaccineStockService.deleteStockById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}