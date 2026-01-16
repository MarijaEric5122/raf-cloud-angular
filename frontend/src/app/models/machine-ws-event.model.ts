export interface MachineWsEvent {
  machineId: number;
  state: string;
  busy: boolean;
  busyAction: string | null;
}
