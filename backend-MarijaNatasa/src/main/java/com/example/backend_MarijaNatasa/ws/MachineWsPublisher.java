package com.example.backend_MarijaNatasa.ws;

import com.example.backend_MarijaNatasa.dto.MachineWsEvent;
import com.example.backend_MarijaNatasa.machine.Machine;
import com.example.backend_MarijaNatasa.machine.MachineRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MachineWsPublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final MachineRepository machineRepository;

    public MachineWsPublisher(SimpMessagingTemplate messagingTemplate, MachineRepository machineRepository) {
        this.messagingTemplate = messagingTemplate;
        this.machineRepository = machineRepository;
    }

    @Transactional(readOnly = true)
    public void publishMachine(Long machineId) {
        Machine m = machineRepository.findById(machineId).orElse(null);
        if (m == null) return;

        MachineWsEvent event = new MachineWsEvent(
                m.getId(),
                m.getState(),
                m.getBusy(),
                m.getBusyAction()
        );

        // svi klijenti slušaju ovo
        messagingTemplate.convertAndSend("/topic/machines", event);

        // (opciono) i per-mašina kanal
        messagingTemplate.convertAndSend("/topic/machines/" + machineId, event);
    }
}
