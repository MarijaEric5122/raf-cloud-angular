package com.example.backend_MarijaNatasa.machine;

import com.example.backend_MarijaNatasa.ws.MachineWsPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MachineOperationRunner {

    private final MachineRepository machineRepository;
    private final MachineWsPublisher publisher;

    public MachineOperationRunner(MachineRepository machineRepository,
                                  MachineWsPublisher publisher) {
        this.machineRepository = machineRepository;
        this.publisher = publisher;
    }

    private void simulateLongOperation() {
        try {
            long delay = 10_000L + (long) (Math.random() * 5_000L);
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {}
    }

    private void sleepRandom(long baseMillis) {
        try {
            long delta = (long) (Math.random() * 2_000L);
            Thread.sleep(baseMillis + delta);
        } catch (InterruptedException ignored) {}
    }

    @Async
    @Transactional
    public void runStart(Long machineId) {
        try {
            simulateLongOperation();

            Machine m = machineRepository.findById(machineId)
                    .orElseThrow(() -> new RuntimeException("Mašina ne postoji (start background)"));

            m.setState("UPALJENA");
            machineRepository.saveAndFlush(m);

            // ✅ javi da je state promenjen
            publisher.publishMachine(machineId);

        } finally {
            machineRepository.releaseMachine(machineId);

            // ✅ javi da više nije busy
            publisher.publishMachine(machineId);
        }
    }

    @Async
    @Transactional
    public void runStop(Long machineId) {
        try {
            simulateLongOperation();

            Machine m = machineRepository.findById(machineId)
                    .orElseThrow(() -> new RuntimeException("Mašina ne postoji (stop background)"));

            m.setState("UGASENA");
            machineRepository.saveAndFlush(m);

            publisher.publishMachine(machineId);

        } finally {
            machineRepository.releaseMachine(machineId);
            publisher.publishMachine(machineId);
        }
    }

    @Async
    @Transactional
    public void runRestart(Long machineId) {
        try {
            sleepRandom(6000);

            Machine mOff = machineRepository.findById(machineId)
                    .orElseThrow(() -> new RuntimeException("Mašina ne postoji (restart off)"));

            mOff.setState("UGASENA");
            machineRepository.saveAndFlush(mOff); // ✅ version++

            // ✅ midway notify (UGASENA)
            publisher.publishMachine(machineId);

            sleepRandom(6000);

            Machine mOn = machineRepository.findById(machineId)
                    .orElseThrow(() -> new RuntimeException("Mašina ne postoji (restart on)"));

            mOn.setState("UPALJENA");
            machineRepository.saveAndFlush(mOn); // ✅ version++

            // ✅ done notify (UPALJENA)
            publisher.publishMachine(machineId);

        } finally {
            machineRepository.releaseMachine(machineId);
            publisher.publishMachine(machineId);
        }
    }
}
