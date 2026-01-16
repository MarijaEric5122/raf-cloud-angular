import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  BehaviorSubject,
  Observable,
  Subject,
  switchMap,
  takeUntil,
} from 'rxjs';
import { MachinesErrorLog } from 'src/app/models/machine.model';
import { MachinesErrorLogsService } from 'src/app/services/machines-error-logs.service';
import { WebsocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'app-machines-error-logs-list',
  templateUrl: './machines-error-logs-list.component.html',
  styleUrls: ['./machines-error-logs-list.component.css'],
})
export class MachinesErrorLogsListComponent implements OnInit, OnDestroy {
  machinesErrorLogs$!: Observable<MachinesErrorLog[]>;

  private refreshTrigger = new BehaviorSubject<void>(undefined);
  private destroy$ = new Subject<void>();

  constructor(
    private errorService: MachinesErrorLogsService,
    private ws: WebsocketService
  ) {}

  ngOnInit(): void {
    // initial load and refresh
    this.machinesErrorLogs$ = this.refreshTrigger.pipe(
      switchMap(() => this.errorService.getAllVisible())
    );

    // WS connect
    this.ws.connect();

    // trigger on /topic/errors refresh
    this.ws.errorsRefresh$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.refreshTrigger.next());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
