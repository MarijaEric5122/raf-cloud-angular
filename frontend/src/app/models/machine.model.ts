export type MachineState = 'UPALJENA' | 'UGASENA';
export type MachineOperation = 'START' | 'STOP' | 'RESTART';
export interface MachineSchedule {
  date: string;
  time: string;
  operation: MachineOperation;
}

export interface MachineCreator {
  id: number;
  name?: string;
  email?: string;
}

export interface Machine {
  id: number;
  name: string;
  type: string;
  description?: string | null;

  state: MachineState;
  active: boolean;

  createdAt: string;

  busy: boolean;
  busyAction?: MachineOperation | null;

  createdById?: number | null;
  createdByEmail?: string | null;

  schedule?: MachineSchedule | null;

  version?: number;
}

export type ErrorOperation = 'START' | 'STOP' | 'RESTART';

export interface MachinesErrorLog {
  id: number;
  machineId: number;
  operation: ErrorOperation;
  message: string;
  date: string;
  userId: number;
}

export type ScheduleStatus = 'PENDING' | 'RUNNING' | 'DONE' | 'FAILED';

export interface MachineScheduleRow {
  id: number;
  machineId: number;
  operation: 'START' | 'STOP' | 'RESTART';
  runAt: string;
  status: ScheduleStatus;
  errorMessage?: string | null;
  createdById?: number | null;
}
