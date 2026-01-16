package com.example.backend.machine.machine_schedule;

import com.example.backend.security.CheckPermission;
import com.example.backend.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/machine-schedule")
@CrossOrigin
public class MachineScheduleController {

    private final MachineScheduleService scheduleService;

    public MachineScheduleController(MachineScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    private User getCurrentUser() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();

        Object userAttr = request.getAttribute("user");
        if (userAttr == null) {
            throw new RuntimeException("Korisnik nije ulogovan (nema 'user' atribut u requestu)");
        }
        return (User) userAttr;
    }

    public static class MachineScheduleRowDto {
        public Long id;
        public Long machineId;
        public String operation;
        public LocalDateTime runAt;
        public String status;
        public String errorMessage;
        public Long createdById;
    }

    @CheckPermission("CAN_READ_SCHEDULES")
    @GetMapping
    public List<MachineScheduleRowDto> getSchedules() {
        User current = getCurrentUser();
        boolean isAdmin = current.getPermissions().contains("is_admin");

        return scheduleService.getSchedulesForUser(current, isAdmin)
                .stream()
                .map(s -> {
                    MachineScheduleRowDto dto = new MachineScheduleRowDto();
                    dto.id = s.getId();
                    dto.machineId = s.getMachine().getId();
                    dto.operation = s.getOperation();
                    dto.runAt = s.getRunAt();
                    dto.status = s.getStatus();
                    dto.errorMessage = s.getErrorMessage();
                    dto.createdById = (s.getCreatedBy() != null) ? s.getCreatedBy().getId() : null;
                    return dto;
                })
                .toList();
    }
}
