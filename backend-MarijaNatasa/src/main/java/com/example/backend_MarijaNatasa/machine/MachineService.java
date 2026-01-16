package com.example.backend_MarijaNatasa.machine;

import com.example.backend_MarijaNatasa.user.User;
import com.example.backend_MarijaNatasa.ws.MachineWsPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MachineService {

    private final MachineRepository machineRepository;
    private final MachineOperationRunner runner;
    private final MachineWsPublisher publisher;

    public MachineService(MachineRepository machineRepository,
                          MachineOperationRunner runner,
                          MachineWsPublisher publisher) {
        this.machineRepository = machineRepository;
        this.runner = runner;
        this.publisher = publisher;
    }

    // ================== SEARCH (kako si imala) ==================

    public List<Machine> getMachinesForUser(User currentUser, boolean isAdmin) {
        if (isAdmin) {
            return machineRepository.findByActiveTrue();
        } else {
            return machineRepository.findByCreatedBy_IdAndActiveTrue(currentUser.getId());
        }
    }

    public List<Machine> searchMachines(
            User currentUser,
            boolean isAdmin,
            String filterName,
            List<String> states,
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<Machine> source = getMachinesForUser(currentUser, isAdmin);

        return source.stream()
                .filter(m -> {
                    if (filterName != null && !filterName.trim().isEmpty()) {
                        return m.getName().toLowerCase().contains(filterName.toLowerCase());
                    }
                    return true;
                })
                .filter(m -> {
                    if (states != null && !states.isEmpty()) {
                        return states.contains(m.getState());
                    }
                    return true;
                })
                .filter(m -> {
                    if (startDate != null) {
                        LocalDateTime from = startDate.atStartOfDay();
                        return m.getCreatedAt().isEqual(from) || m.getCreatedAt().isAfter(from);
                    }
                    return true;
                })
                .filter(m -> {
                    if (endDate != null) {
                        LocalDateTime to = endDate.atTime(LocalTime.MAX);
                        return m.getCreatedAt().isBefore(to) || m.getCreatedAt().isEqual(to);
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    // ================== CREATE / DESTROY ==================

    @Transactional
    public Machine createMachine(User currentUser, String name, String type, String description) {
        Machine m = new Machine();
        m.setName(name);
        m.setType(type);
        m.setDescription(description);
        m.setState("UGASENA");
        m.setCreatedBy(currentUser);
        m.setActive(true);
        m.setCreatedAt(LocalDateTime.now());
        m.setBusy(false);
        m.setBusyAction(null);
        return machineRepository.save(m);
    }

    @Transactional
    public void destroyMachine(Long id) {
        try {
            Machine m = machineRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Mašina ne postoji"));

            if (!"UGASENA".equals(m.getState())) {
                throw new RuntimeException("Mašina mora biti UGASENA pre uništavanja");
            }

            if (Boolean.TRUE.equals(m.getBusy())) {
                String op = machineRepository.findBusyActionById(id);
                throw new RuntimeException("Mašina je zauzeta" + (op != null ? (" (" + op + " u toku)") : ""));
            }


            m.setActive(false);
            machineRepository.save(m);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException(
                    "Mašina je u međuvremenu izmenjena od strane druge operacije (optimistic lock). Pokušaj ponovo.",
                    e
            );
        }
    }

    // ================== START / STOP / RESTART ==================

    @Transactional
    public void startMachine(Long id) {
        Machine m = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mašina ne postoji"));

        if (!"UGASENA".equals(m.getState())) {
            throw new RuntimeException("Mašina mora biti UGASENA da bi se upalila");
        }

        int claimed = machineRepository.claimMachine(id, "START");
        if (claimed == 0) {
            String op = machineRepository.findBusyActionById(id);
            throw new RuntimeException("Mašina je zauzeta" + (op != null ? (" (" + op + " u toku)") : ""));
        }

        publisher.publishMachine(id); // ✅ sad je busy=true + busyAction="START"
        // async posao
        runner.runStart(id);
    }

    @Transactional
    public void stopMachine(Long id) {
        Machine m = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mašina ne postoji"));

        if (!"UPALJENA".equals(m.getState())) {
            throw new RuntimeException("Mašina mora biti UPALJENA da bi se ugasila");
        }

        int claimed = machineRepository.claimMachine(id, "STOP");
        if (claimed == 0) {
            String op = machineRepository.findBusyActionById(id);
            throw new RuntimeException("Mašina je zauzeta" + (op != null ? (" (" + op + " u toku)") : ""));
        }

        publisher.publishMachine(id);
        runner.runStop(id);
    }

    @Transactional
    public void restartMachine(Long id) {
        Machine m = machineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mašina ne postoji"));

        if (!"UPALJENA".equals(m.getState())) {
            throw new RuntimeException("Mašina mora biti UPALJENA da bi se restartovala");
        }

        int claimed = machineRepository.claimMachine(id, "RESTART");
        if (claimed == 0) {
            String op = machineRepository.findBusyActionById(id);
            throw new RuntimeException("Mašina je zauzeta" + (op != null ? (" (" + op + " u toku)") : ""));
        }

        publisher.publishMachine(id); // ✅ sad je busy=true + busyAction="START"
        runner.runRestart(id);
    }
}
