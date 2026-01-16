package com.example.backend.machine.machine_schedule;

import com.example.backend.machine.Machine;
import com.example.backend.machine.MachineRepository;
import com.example.backend.user.User;
import com.example.backend.ws.ScheduleWsPublisher;
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

        // normalize action to START/STOP/RESTART
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

        // refresh schedule list
        scheduleWsPublisher.publishRefresh();
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void processDueSchedules() {
        LocalDateTime now = LocalDateTime.now();

        List<MachineSchedule> due =
                scheduleRepository.findByStatusAndRunAtLessThanEqual("PENDING", now);

        for (MachineSchedule s : due) {
            // claim schedule atomically
            int claimed = scheduleRepository.claimSchedule(s.getId());
            if (claimed == 0) {
                continue; // already claimed
            }

            // run async in separate bean
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
