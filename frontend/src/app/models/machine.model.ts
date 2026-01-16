export type MachineState = 'UPALJENA' | 'UGASENA';
export type MachineOperation = 'START' | 'STOP' | 'RESTART';
export interface MachineSchedule {
  date: string; // npr "2026-01-10" (zavisi kako backend salje)
  time: string; // npr "10:00"
  operation: MachineOperation;
}

/**
 * Opcioni "creator" ako jednog dana backend krene da vraca ceo User objekat.
 * Za sad je dovoljno createdById.
 */
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

  // Backend ti salje createdAt kao string (ISO ili timestamp string)
  // Angular date pipe radi sa stringom i brojem, pa je ovo najbezbolnije:
  createdAt: string;

  // BUSY INFO (backend ti ovo vec salje)
  busy: boolean;
  busyAction?: MachineOperation | null;

  // Creator info (zavisno sta backend vraca)
  createdById?: number | null;
  createdByEmail?: string | null;

  schedule?: MachineSchedule | null;

  // ako backend salje optimistic lock version:
  version?: number;
}

export type ErrorOperation = 'START' | 'STOP' | 'RESTART';

export interface MachinesErrorLog {
  id: number;
  machineId: number;
  operation: ErrorOperation; // backend šalje START/STOP/RESTART
  message: string;
  date: string; // LocalDateTime iz Springa dolazi kao string
  userId: number; // backend šalje userId
}

export type ScheduleStatus = 'PENDING' | 'RUNNING' | 'DONE' | 'FAILED';

export interface MachineScheduleRow {
  id: number;
  machineId: number;
  operation: 'START' | 'STOP' | 'RESTART'; // ili UPALI/UGASI/RESTARTUJ ako tako čuvaš
  runAt: string; // ISO string
  status: ScheduleStatus;
  errorMessage?: string | null;
  createdById?: number | null;
}
