package com.example.backend.ws;

import com.example.backend.dto.MachineWsEvent;
import com.example.backend.machine.Machine;
import com.example.backend.machine.MachineRepository;
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

        // broadcast to all clients
        messagingTemplate.convertAndSend("/topic/machines", event);

        // optional per-machine channel
        messagingTemplate.convertAndSend("/topic/machines/" + machineId, event);
    }
}
