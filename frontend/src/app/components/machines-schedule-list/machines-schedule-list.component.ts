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
    // 1) učitaj odmah + svaki put kad okinemo refreshTrigger -> radi GET
    this.schedules$ = this.refreshTrigger.pipe(
      switchMap(() => this.machinesService.getSchedules())
    );

    // 2) WS connect (ako već nije konektovan)
    this.ws.connect();

    // 3) kad backend pošalje "refresh" na /topic/schedules -> okini trigger
    this.ws.schedulesRefresh$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.refreshTrigger.next());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
