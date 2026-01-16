import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { MachinesErrorLog } from 'src/app/models/machine.model';
import { MachinesErrorLogsService } from 'src/app/services/machines-error-logs.service';

@Component({
  selector: 'app-machines-error-logs-list',
  templateUrl: './machines-error-logs-list.component.html',
  styleUrls: ['./machines-error-logs-list.component.css'],
})
export class MachinesErrorLogsListComponent implements OnInit {
  machinesErrorLogs$!: Observable<MachinesErrorLog[]>;

  constructor(private errorService: MachinesErrorLogsService) {}

  ngOnInit(): void {
    this.machinesErrorLogs$ = this.errorService.getAllVisible();
  }
}
