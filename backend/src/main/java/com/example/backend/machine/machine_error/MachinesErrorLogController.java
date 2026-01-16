package com.example.backend.machine.machine_error;

import com.example.backend.security.CheckPermission;
import com.example.backend.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@RestController
@RequestMapping("/api/machine-error-logs")
@CrossOrigin
public class MachinesErrorLogController {

    private final MachinesErrorLogService errorLogService;

    public MachinesErrorLogController(MachinesErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

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

    @CheckPermission("CAN_READ_ERRORS")
    @GetMapping
    public List<MachinesErrorLog> getLogs() {
        User current = getCurrentUser();
        boolean isAdmin = current.getPermissions().contains("is_admin");
        return errorLogService.getLogsForUser(current, isAdmin);
    }
}
