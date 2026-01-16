package com.example.backend.machine.machine_error;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MachinesErrorLogRepository extends JpaRepository<MachinesErrorLog, Long> {

    // admin view
    List<MachinesErrorLog> findAllByOrderByDateDesc();

    // user view
    List<MachinesErrorLog> findByMachineIdInOrderByDateDesc(List<Long> machineIds);
}
