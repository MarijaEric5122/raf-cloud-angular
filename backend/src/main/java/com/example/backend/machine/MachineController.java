package com.example.backend.machine;

import com.example.backend.machine.machine_schedule.MachineScheduleService;
import com.example.backend.security.CheckPermission;
import com.example.backend.user.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.example.backend.machine.machine_error.MachinesErrorLogService;


import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/machines")
@CrossOrigin
public class MachineController {

    private final MachineService machineService;
    private final MachineScheduleService machineScheduleService;
    private final MachinesErrorLogService machinesErrorLogService;

    public MachineController(MachineService machineService,
                             MachineScheduleService machineScheduleService,
                             MachinesErrorLogService machinesErrorLogService) {
        this.machineService = machineService;
        this.machineScheduleService = machineScheduleService;
        this.machinesErrorLogService = machinesErrorLogService;
    }

    // Resolve current user from request attribute set by JwtFilter.
    private User getCurrentUser() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder
                        .currentRequestAttributes())
                        .getRequest();

        Object userAttr = request.getAttribute("user");
        if (userAttr == null) {
            throw new RuntimeException("Korisnik nije ulogovan (nema 'user' atribut u requestu)");
        }

        return (User) userAttr;
    }

    @CheckPermission("CAN_SEARCH_MACHINES")
    @GetMapping
    public List<Machine> searchMachines(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        User current = getCurrentUser();
        boolean isAdmin = current.getPermissions().contains("is_admin");

        LocalDate from = (startDate != null && !startDate.isBlank()) ? LocalDate.parse(startDate) : null;
        LocalDate to   = (endDate != null && !endDate.isBlank()) ? LocalDate.parse(endDate) : null;

        return machineService.searchMachines(current, isAdmin, name, states, from, to);
    }

    @CheckPermission("CAN_CREATE_MACHINES")
    @PostMapping
    public Machine createMachine(@RequestBody MachineCreateRequest body) {
        User current = getCurrentUser();
        return machineService.createMachine(
                current,
                body.getName(),
                body.getType(),
                body.getDescription()
        );
    }

    @CheckPermission("CAN_DESTROY_MACHINES")
    @DeleteMapping("/{id}")
    public void destroyMachine(@PathVariable Long id) {
        User current = getCurrentUser();
        try {
            machineService.destroyMachine(id);
        } catch (RuntimeException e) {
            machinesErrorLogService.logError(
                    id,
                    "DESTROY",
                    e.getMessage(),
                    current.getId()
            );
            throw e;
        }
    }

    @CheckPermission("CAN_START_MACHINES")
    @PostMapping("/{id}/start")
    public void startMachine(@PathVariable Long id) {
        User current = getCurrentUser();
        try {
            machineService.startMachine(id);
        } catch (RuntimeException e) {
            machinesErrorLogService.logError(
                    id,
                    "START",
                    e.getMessage(),
                    current.getId()
            );
            throw e;
        }
    }

    @CheckPermission("CAN_STOP_MACHINES")
    @PostMapping("/{id}/stop")
    public void stopMachine(@PathVariable Long id) {
        User current = getCurrentUser();
        try {
            machineService.stopMachine(id);
        } catch (RuntimeException e) {
            machinesErrorLogService.logError(
                    id,
                    "STOP",
                    e.getMessage(),
                    current.getId()
            );
            throw e;
        }
    }

    @CheckPermission("CAN_RESTART_MACHINES")
    @PostMapping("/{id}/restart")
    public void restartMachine(@PathVariable Long id) {
        User current = getCurrentUser();
        try {
            machineService.restartMachine(id);
        } catch (RuntimeException e) {
            machinesErrorLogService.logError(
                    id,
                    "RESTART",
                    e.getMessage(),
                    current.getId()
            );
            throw e;
        }
    }


    @CheckPermission("CAN_READ_SCHEDULES")
    @PostMapping("/{id}/schedule")
    public void scheduleMachine(
            @PathVariable Long id,
            @RequestBody MachineScheduleRequest body
    ) {
        User current = getCurrentUser();

        LocalDate date = LocalDate.parse(body.getDate());
        LocalTime time = LocalTime.parse(body.getTime());

        // Save PENDING schedule only.
        machineScheduleService.scheduleMachine(
                id,
                body.getAction(),
                date,
                time,
                current
        );
    }


    public static class MachineCreateRequest {
        private String name;
        private String type;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class MachineScheduleRequest {
        private String action;
        private String date;
        private String time;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

}
