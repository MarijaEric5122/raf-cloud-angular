import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  BehaviorSubject,
  Observable,
  Subject,
  takeUntil,
  switchMap,
} from 'rxjs';
import { MachinesService } from '../../services/machines.service';
import { MachineScheduleRow } from '../../models/machine.model';
import { WebsocketService } from '../../services/websocket.service';

@Component({
  selector: 'app-machines-schedule-list',
  templateUrl: './machines-schedule-list.component.html',
})
export class MachinesScheduleListComponent implements OnInit, OnDestroy {
  schedules$!: Observable<MachineScheduleRow[]>;

  private refreshTrigger = new BehaviorSubject<void>(undefined);
  private destroy$ = new Subject<void>();

  constructor(
    private machinesService: MachinesService,
    private ws: WebsocketService
  ) {}

  ngOnInit(): void {
    // initial load and refresh
    this.schedules$ = this.refreshTrigger.pipe(
      switchMap(() => this.machinesService.getSchedules())
    );

    // WS connect if needed
    this.ws.connect();

    // trigger on /topic/schedules refresh
    this.ws.schedulesRefresh$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.refreshTrigger.next());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
