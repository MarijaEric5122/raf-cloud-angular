package com.example.backend_MarijaNatasa.machine;

import com.example.backend_MarijaNatasa.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Version;
import jakarta.persistence.Column;
import com.fasterxml.jackson.annotation.JsonProperty;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "machines")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Naziv mašine – obavezan
    @Column(nullable = false)
    private String name;

    // Tip mašine – obavezan
    @Column(nullable = false)
    private String type;

    // Opis – opciono polje
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Stanje mašine.
     * Koristim String da bih mogao direktno da čuvam vrednosti
     * koje koristi frontend: "UPALJENA", "UGASENA"".
     */
    @Column(nullable = false)
    private String state;

    /**
     * Ko je kreirao mašinu.
     * ManyToOne jer jedan user može da ima više mašina.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @JsonIgnore
    private User createdBy;

    /**
     * "Soft delete" polje – kada uništimo mašinu, ne brišemo je iz baze
     * nego stavimo active = false, pa je ne prikazujemo u pretrazi.
     */
    @Column(nullable = false)
    private Boolean active = true;

    // Datum i vreme kada je mašina kreirana.

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean busy = false;

    @Column
    private String busyAction; // "START", "STOP", "RESTART"

    @Version
    @Column(nullable = false)
    private Long version;

    // ✅ šalje FK id kreatora u JSON (ne dira lazy loading)
    @JsonProperty("createdById")
    public Long getCreatedById() {
        return createdBy != null ? createdBy.getId() : null;
    }

    // ✅ ako želiš da u tabeli vidiš ko je (email)
// Napomena: ovo može da okine lazy load (jedan dodatni query po mašini)
    @JsonProperty("createdByEmail")
    public String getCreatedByEmail() {
        return createdBy != null ? createdBy.getEmail() : null;
    }


    // ===== GETTERI I SETTERI =====


    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getBusy() {
        return busy;
    }

    public void setBusy(Boolean busy) {
        this.busy = busy;
    }

    public String getBusyAction() {
        return busyAction;
    }

    public void setBusyAction(String busyAction) {
        this.busyAction = busyAction;
    }
}
