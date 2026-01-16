import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import {
  BehaviorSubject,
  combineLatest,
  debounceTime,
  Observable,
  of,
  startWith,
  switchMap,
  Subject,
  takeUntil,
} from 'rxjs';
import { Machine, MachineState } from '../../models/machine.model';
import { AuthService } from '../../services/auth.service';
import { MachinesService } from '../../services/machines.service';
import { WebsocketService } from '../../services/websocket.service';

@Component({
  selector: 'app-machines-search',
  templateUrl: './machines-search.component.html',
})
export class MachinesSearchComponent implements OnInit, OnDestroy {
  machines$!: Observable<Machine[]>;
  canSearchMachines$!: Observable<boolean>;
  canCreateMachine$!: Observable<boolean>;
  canDestroyMachine$!: Observable<boolean>;
  canStartMachine$!: Observable<boolean>;
  canStopMachine$!: Observable<boolean>;
  canRestartMachine$!: Observable<boolean>;
  canSchedule$!: Observable<boolean>;

  searchForm: FormGroup;
  allStates: MachineState[] = ['UPALJENA', 'UGASENA'];

  private refreshTrigger = new BehaviorSubject<void>(undefined);
  private destroy$ = new Subject<void>();

  constructor(
    private machineService: MachinesService,
    private authService: AuthService,
    private fb: FormBuilder,
    private ws: WebsocketService
  ) {
    this.searchForm = this.fb.group({
      name: [''],
      states: [[] as MachineState[]],
      startDate: [''],
      endDate: [''],
    });
  }

  ngOnInit(): void {
    this.canSearchMachines$ = this.authService.hasPermission(
      'CAN_SEARCH_MACHINES'
    );
    this.canCreateMachine$ = this.authService.hasPermission(
      'CAN_CREATE_MACHINES'
    );
    this.canDestroyMachine$ = this.authService.hasPermission(
      'CAN_DESTROY_MACHINES'
    );
    this.canStartMachine$ =
      this.authService.hasPermission('CAN_START_MACHINES');
    this.canStopMachine$ = this.authService.hasPermission('CAN_STOP_MACHINES');
    this.canRestartMachine$ = this.authService.hasPermission(
      'CAN_RESTART_MACHINES'
    );
    this.canSchedule$ = this.authService.hasPermission('CAN_READ_SCHEDULES');

    const formChanges$ = this.searchForm.valueChanges.pipe(
      startWith(this.searchForm.value)
    );

    this.machines$ = combineLatest([
      this.authService.authUser$,
      formChanges$,
      this.refreshTrigger,
    ]).pipe(
      debounceTime(300),
      switchMap(([user, formValue]) => {
        if (!user) return of([]);
        return this.machineService.search(formValue);
      })
    );

    // WS connect + refresh on event
    this.ws.connect();

    this.ws.events$.pipe(takeUntil(this.destroy$)).subscribe((ev) => {
      if (!ev) return;
      this.refreshTrigger.next();
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    // opcionalno: ako ti ne treba WS kad odeš sa stranice
    //this.ws.disconnect();
  }

  toggleState(state: MachineState): void {
    const currentStates = (this.searchForm.get('states')?.value ??
      []) as MachineState[];
    const newStates = currentStates.includes(state)
      ? currentStates.filter((s) => s !== state)
      : [...currentStates, state];
    this.searchForm.get('states')?.setValue(newStates);
  }

  isStateSelected(state: MachineState): boolean {
    return (
      (this.searchForm.get('states')?.value ?? []) as MachineState[]
    ).includes(state);
  }

  delete(id: number): void {
    this.machineService.destroy(id).subscribe(() => this.refreshTrigger.next());
  }

  start(id: number): void {
    this.machineService
      .startMachine(id)
      .subscribe(() => this.refreshTrigger.next());
  }

  stop(id: number): void {
    this.machineService
      .stopMachine(id)
      .subscribe(() => this.refreshTrigger.next());
  }

  restart(id: number): void {
    this.machineService
      .restartMachine(id)
      .subscribe(() => this.refreshTrigger.next());
  }
}
