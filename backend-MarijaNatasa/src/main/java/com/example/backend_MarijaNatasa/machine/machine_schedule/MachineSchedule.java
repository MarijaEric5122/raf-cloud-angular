package com.example.backend_MarijaNatasa.machine.machine_schedule;

import com.example.backend_MarijaNatasa.machine.Machine;
import com.example.backend_MarijaNatasa.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "machine_schedule")
public class MachineSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Koja mašina
    @ManyToOne
    @JoinColumn(name = "machine_id")
    private Machine machine;

    // Kada treba da se izvrši
    private LocalDateTime runAt;

    // "UPALI", "UGASI", "RESTARTUJ"
    private String operation;

    // "PENDING", "RUNNING", "DONE", "FAILED"
    private String status;

    // poruka ako padne (npr. "Mašina nije bila UGASENA...")
    private String errorMessage;

    // Ko je zakazao (nije obavezno, ali lepo je imati)
    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    // getteri/setteri

    public Long getId() {
        return id;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public LocalDateTime getRunAt() {
        return runAt;
    }

    public void setRunAt(LocalDateTime runAt) {
        this.runAt = runAt;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}

