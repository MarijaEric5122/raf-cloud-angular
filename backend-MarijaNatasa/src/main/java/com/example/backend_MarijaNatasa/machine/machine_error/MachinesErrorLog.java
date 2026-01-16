package com.example.backend_MarijaNatasa.machine.machine_error;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "machine_error_logs")
public class MachinesErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // kada se desila greška
    @Column(nullable = false)
    private LocalDateTime date;

    // ID mašine na kojoj je puko
    @Column(nullable = false)
    private Long machineId;

    // START / STOP / RESTART
    @Column(nullable = false)
    private String operation;

    // poruka greške (za prikaz u tabeli)
    @Column(nullable = false, length = 1000)
    private String message;

    // ko je pokrenuo / zakazao (korisnik)
    @Column(nullable = false)
    private Long userId;

    // --- getteri / setteri ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
