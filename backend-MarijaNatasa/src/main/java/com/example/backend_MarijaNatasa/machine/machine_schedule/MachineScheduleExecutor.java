package com.example.backend_MarijaNatasa.machine.machine_schedule;

import com.example.backend_MarijaNatasa.machine.MachineService;
import com.example.backend_MarijaNatasa.machine.machine_error.MachinesErrorLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MachineScheduleExecutor {

    private final MachineScheduleRepository scheduleRepository;
    private final MachineService machineService;
    private final MachinesErrorLogService errorLogService;

    public MachineScheduleExecutor(MachineScheduleRepository scheduleRepository,
                                   MachineService machineService,
                                   MachinesErrorLogService errorLogService) {
        this.scheduleRepository = scheduleRepository;
        this.machineService = machineService;
        this.errorLogService = errorLogService;
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

            // Bitno: MachineService ti sada vraća odmah (jer stvar radi async),
            // pa DONE znači: "pokušaj pokrenut uspešno", ne "završilo se".
            schedule.setStatus("DONE");
            schedule.setErrorMessage(null);

        } catch (RuntimeException ex) {
            schedule.setStatus("FAILED");
            schedule.setErrorMessage(ex.getMessage());

            // obavezno logovanje greške za schedule (zahtev projekta)
            errorLogService.logError(
                    schedule.getMachine().getId(),
                    schedule.getOperation(),
                    ex.getMessage(),
                    schedule.getCreatedBy() != null ? schedule.getCreatedBy().getId() : null
            );
        }

        scheduleRepository.save(schedule);
    }
}
