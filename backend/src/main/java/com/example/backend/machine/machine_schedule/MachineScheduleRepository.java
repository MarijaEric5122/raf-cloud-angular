package com.example.backend.machine.machine_schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MachineScheduleRepository extends JpaRepository<MachineSchedule, Long> {

    List<MachineSchedule> findByStatusAndRunAtLessThanEqual(String status, LocalDateTime time);

    // Prevent double-processing
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update MachineSchedule s set s.status = 'RUNNING' where s.id = :id and s.status = 'PENDING'")
    int claimSchedule(@Param("id") Long id);

    // admin view
    List<MachineSchedule> findAllByOrderByRunAtDesc();

    // user view
    List<MachineSchedule> findByMachine_CreatedBy_IdAndMachine_ActiveTrueOrderByRunAtDesc(Long userId);

}
