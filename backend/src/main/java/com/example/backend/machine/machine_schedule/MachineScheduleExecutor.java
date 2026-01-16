package com.example.backend.machine.machine_schedule;

import com.example.backend.machine.MachineService;
import com.example.backend.machine.machine_error.MachinesErrorLogService;
import com.example.backend.ws.ScheduleWsPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MachineScheduleExecutor {

    private final MachineScheduleRepository scheduleRepository;
    private final MachineService machineService;
    private final MachinesErrorLogService errorLogService;
    private final ScheduleWsPublisher scheduleWsPublisher;

    public MachineScheduleExecutor(MachineScheduleRepository scheduleRepository,
                                   MachineService machineService,
                                   MachinesErrorLogService errorLogService,
                                   ScheduleWsPublisher scheduleWsPublisher) {
        this.scheduleRepository = scheduleRepository;
        this.machineService = machineService;
        this.errorLogService = errorLogService;
        this.scheduleWsPublisher = scheduleWsPublisher;
    }

    @Async
    public void executeOne(Long scheduleId) {
        MachineSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule ne postoji"));

        try {
            Long machineId = schedule.getMachine().getId();

            switch (schedule.getOperation()) {
                case "START":
                    machineService.startMachine(machineId);
                    break;
                case "STOP":
                    machineService.stopMachine(machineId);
                    break;
                case "RESTART":
                    machineService.restartMachine(machineId);
                    break;
                default:
                    throw new RuntimeException("Nepoznata akcija: " + schedule.getOperation());
            }

            // DONE means request started, not finished.
            schedule.setStatus("DONE");
            schedule.setErrorMessage(null);

        } catch (RuntimeException ex) {
            schedule.setStatus("FAILED");
            schedule.setErrorMessage(ex.getMessage());

            // Log schedule failure.
            errorLogService.logError(
                    schedule.getMachine().getId(),
                    schedule.getOperation(),
                    ex.getMessage(),
                    schedule.getCreatedBy() != null ? schedule.getCreatedBy().getId() : null
            );
        }

        scheduleRepository.save(schedule);

        // refresh schedule list after status change
        scheduleWsPublisher.publishRefresh();
    }
}
