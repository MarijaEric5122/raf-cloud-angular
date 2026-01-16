import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { MachinesService } from '../../services/machines.service';
import { MachineScheduleRow } from '../../models/machine.model';

@Component({
  selector: 'app-machines-schedule-list',
  templateUrl: './machines-schedule-list.component.html',
})
export class MachinesScheduleListComponent implements OnInit {
  schedules$!: Observable<MachineScheduleRow[]>;

  constructor(private machinesService: MachinesService) {}

  ngOnInit(): void {
    this.schedules$ = this.machinesService.getSchedules();
  }
}
