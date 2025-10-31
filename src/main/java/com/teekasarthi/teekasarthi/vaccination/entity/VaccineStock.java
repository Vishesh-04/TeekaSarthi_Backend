package com.teekasarthi.teekasarthi.vaccination.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

// This annotation marks this class as a JPA entity,
// which means Spring Data JPA will manage it in the database.
@Entity
public class VaccineStock {

    // @Id marks this field as the primary key.
    // @GeneratedValue specifies how the ID is generated (e.g., auto-increment).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int currentStock;
    private int usedStock;
    private int receivedStock;
    private LocalDate expiryDate;
    private String status;

    // JPA requires a no-argument constructor.
    public VaccineStock() {
    }

    // --- Getters and Setters ---
    // These methods are necessary for JPA and other frameworks
    // to access the properties of your entity.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getUsedStock() {
        return usedStock;
    }

    public void setUsedStock(int usedStock) {
        this.usedStock = usedStock;
    }

    public int getReceivedStock() {
        return receivedStock;
    }

    public void setReceivedStock(int receivedStock) {
        this.receivedStock = receivedStock;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
