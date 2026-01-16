package com.example.backend_MarijaNatasa.machine.machine_schedule;

import com.example.backend_MarijaNatasa.machine.Machine;
import com.example.backend_MarijaNatasa.machine.MachineRepository;
import com.example.backend_MarijaNatasa.user.User;
import com.example.backend_MarijaNatasa.ws.ScheduleWsPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class MachineScheduleService {

    private final MachineRepository machineRepository;
    private final MachineScheduleRepository scheduleRepository;
    private final MachineScheduleExecutor executor;
    private final ScheduleWsPublisher scheduleWsPublisher;

    public MachineScheduleService(MachineRepository machineRepository,
                                  MachineScheduleRepository scheduleRepository,
                                  MachineScheduleExecutor executor,
                                  ScheduleWsPublisher scheduleWsPublisher) {
        this.machineRepository = machineRepository;
        this.scheduleRepository = scheduleRepository;
        this.executor = executor;
        this.scheduleWsPublisher = scheduleWsPublisher;
    }

    public void scheduleMachine(Long machineId,
                                String action,
                                LocalDate date,
                                LocalTime time,
                                User currentUser) {

        // normalizuj akciju da bude START/STOP/RESTART
        String op = action.toUpperCase().trim();

        if (!op.equals("START") && !op.equals("STOP") && !op.equals("RESTART")) {
            throw new RuntimeException("Nepoznata akcija: " + action);
        }

        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new RuntimeException("Mašina ne postoji"));

        MachineSchedule schedule = new MachineSchedule();
        schedule.setMachine(machine);
        schedule.setOperation(op);
        schedule.setRunAt(LocalDateTime.of(date, time));
        schedule.setStatus("PENDING");
        schedule.setCreatedBy(currentUser);

        scheduleRepository.save(schedule);

        // WS refresh signal za listu schedulera
        scheduleWsPublisher.publishRefresh();
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void processDueSchedules() {
        LocalDateTime now = LocalDateTime.now();

        List<MachineSchedule> due =
                scheduleRepository.findByStatusAndRunAtLessThanEqual("PENDING", now);

        for (MachineSchedule s : due) {
            // opciono atomski claim (PENDING -> RUNNING)
            int claimed = scheduleRepository.claimSchedule(s.getId());
            if (claimed == 0) {
                continue; // neko je već uzeo ovaj schedule
            }

            // sada pusti async izvršenje u DRUGOM beanu (radi @Async)
            executor.executeOne(s.getId());
        }
    }

    public List<MachineSchedule> getSchedulesForUser(User currentUser, boolean isAdmin) {
        if (isAdmin) {
            return scheduleRepository.findAllByOrderByRunAtDesc();
        }

        return scheduleRepository
                .findByMachine_CreatedBy_IdAndMachine_ActiveTrueOrderByRunAtDesc(currentUser.getId());
    }

}
