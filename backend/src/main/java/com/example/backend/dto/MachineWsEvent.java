package com.example.backend.dto;

public class MachineWsEvent {
    private Long machineId;
    private String state;
    private Boolean busy;
    private String busyAction;

    public MachineWsEvent() {}

    public MachineWsEvent(Long machineId, String state, Boolean busy, String busyAction) {
        this.machineId = machineId;
        this.state = state;
        this.busy = busy;
        this.busyAction = busyAction;
    }

    public Long getMachineId() { return machineId; }
    public void setMachineId(Long machineId) { this.machineId = machineId; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Boolean getBusy() { return busy; }
    public void setBusy(Boolean busy) { this.busy = busy; }

    public String getBusyAction() { return busyAction; }
    public void setBusyAction(String busyAction) { this.busyAction = busyAction; }
}
