package com.example.backend_MarijaNatasa.machine.machine_error;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MachinesErrorLogRepository extends JpaRepository<MachinesErrorLog, Long> {

    // admin – sve greške, najnovije prve
    List<MachinesErrorLog> findAllByOrderByDateDesc();

    // običan korisnik – greške za njegove mašine
    List<MachinesErrorLog> findByMachineIdInOrderByDateDesc(List<Long> machineIds);
}
