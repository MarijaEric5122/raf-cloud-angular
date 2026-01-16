package com.example.backend_MarijaNatasa.machine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {

    // Sve aktivne mašine
    List<Machine> findByActiveTrue();

    // Sve aktivne mašine određenog korisnika
    List<Machine> findByCreatedBy_IdAndActiveTrue(Long userId);

    @Modifying
    @Query("update Machine m set m.busy = true, m.busyAction = :action " +
            "where m.id = :id and m.active = true and m.busy = false")
    int claimMachine(@Param("id") Long id, @Param("action") String action);

    @Modifying
    @Query("update Machine m set m.busy = false, m.busyAction = null " +
            "where m.id = :id")
    int releaseMachine(@Param("id") Long id);

    @Query("select m.busyAction from Machine m where m.id = :id")
    String findBusyActionById(@Param("id") Long id);


}
