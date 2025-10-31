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

    @GetMapping
    public List<VaccineStock> getAllStock() {
        return vaccineStockService.findAllStock();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VaccineStock> getStockById(@PathVariable Long id) {
        Optional<VaccineStock> stock = vaccineStockService.findStockById(id);
        return stock.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VaccineStock> createStock(@RequestBody VaccineStock vaccineStock) {
        VaccineStock savedStock = vaccineStockService.saveStock(vaccineStock);
        return new ResponseEntity<>(savedStock, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VaccineStock> updateStock(@PathVariable Long id, @RequestBody VaccineStock updatedStock) {
        return vaccineStockService.findStockById(id)
                .map(existingStock -> {
                    updatedStock.setId(id);
                    VaccineStock savedStock = vaccineStockService.saveStock(updatedStock);
                    return ResponseEntity.ok(savedStock);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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
