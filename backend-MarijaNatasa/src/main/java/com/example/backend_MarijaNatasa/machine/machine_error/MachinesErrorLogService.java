package com.example.backend_MarijaNatasa.machine.machine_error;

import com.example.backend_MarijaNatasa.machine.Machine;
import com.example.backend_MarijaNatasa.machine.MachineRepository;
import com.example.backend_MarijaNatasa.user.User;
import com.example.backend_MarijaNatasa.ws.ErrorLogWsPublisher;
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

    // obična pomoćna metoda – upiši jednu grešku
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

        // WS refresh signal za listu errora
        errorLogWsPublisher.publishRefresh();
    }

    // čitanje za UI – admin vidi sve, ostali svoje mašine
    public List<MachinesErrorLog> getLogsForUser(User currentUser, boolean isAdmin) {
        if (isAdmin) {
            return errorLogRepository.findAllByOrderByDateDesc();
        }

        // običan user – uzmi ID-jeve mašina koje je on kreirao
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
