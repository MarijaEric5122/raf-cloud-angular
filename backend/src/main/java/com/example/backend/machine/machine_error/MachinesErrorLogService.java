package com.example.backend.machine.machine_error;

import com.example.backend.machine.Machine;
import com.example.backend.machine.MachineRepository;
import com.example.backend.user.User;
import com.example.backend.ws.ErrorLogWsPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MachinesErrorLogService {

    private final MachinesErrorLogRepository errorLogRepository;
    private final MachineRepository machineRepository;
    private final ErrorLogWsPublisher errorLogWsPublisher;

    public MachinesErrorLogService(MachinesErrorLogRepository errorLogRepository,
                                   MachineRepository machineRepository,
                                   ErrorLogWsPublisher errorLogWsPublisher) {
        this.errorLogRepository = errorLogRepository;
        this.machineRepository = machineRepository;
        this.errorLogWsPublisher = errorLogWsPublisher;
    }

    // helper to write a single error
    public void logError(Long machineId,
                         String operation,
                         String message,
                         Long userId) {

        MachinesErrorLog log = new MachinesErrorLog();
        log.setDate(LocalDateTime.now());
        log.setMachineId(machineId);
        log.setOperation(operation);
        log.setMessage(message);
        log.setUserId(userId);

        errorLogRepository.save(log);

        // refresh error list
        errorLogWsPublisher.publishRefresh();
    }

    // read logs for UI
    public List<MachinesErrorLog> getLogsForUser(User currentUser, boolean isAdmin) {
        if (isAdmin) {
            return errorLogRepository.findAllByOrderByDateDesc();
        }

        // collect machine IDs for user
        List<Long> myMachineIds = machineRepository
                .findByCreatedBy_IdAndActiveTrue(currentUser.getId())
                .stream()
                .map(Machine::getId)
                .collect(Collectors.toList());

        if (myMachineIds.isEmpty()) {
            return Collections.emptyList();
        }

        return errorLogRepository.findByMachineIdInOrderByDateDesc(myMachineIds);
    }
}
