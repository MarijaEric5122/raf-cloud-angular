import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MachinesService } from '../../services/machines.service';
import { MachineSchedule } from '../../models/machine.model';

@Component({
  selector: 'app-machines-schedule',
  templateUrl: './machines-schedule.component.html',
})
export class MachinesScheduleComponent implements OnInit {
  scheduleForm!: FormGroup;

  machineId: number | null = null;

  // UI akcije (lepo za korisnika)
  actions: Array<'UPALI' | 'UGASI' | 'RESTARTUJ'> = [
    'UPALI',
    'UGASI',
    'RESTARTUJ',
  ];

  submitting = false;
  errorMsg: string | null = null;

  // Mapiranje UI -> backend
  private actionMap: Record<
    'UPALI' | 'UGASI' | 'RESTARTUJ',
    'START' | 'STOP' | 'RESTART'
  > = {
    UPALI: 'START',
    UGASI: 'STOP',
    RESTARTUJ: 'RESTART',
  };

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private machineService: MachinesService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const rawId = this.route.snapshot.paramMap.get('id');
    this.machineId = rawId ? Number(rawId) : null;

    this.scheduleForm = this.fb.group({
      action: ['', Validators.required], // UPALI/UGASI/RESTARTUJ
      date: ['', Validators.required],
      time: ['', Validators.required],
    });
  }

  scheduleAction(): void {
    this.errorMsg = null;

    if (!this.machineId) {
      this.errorMsg = 'Neispravan machineId u ruti.';
      return;
    }

    if (this.scheduleForm.invalid) {
      this.scheduleForm.markAllAsTouched();
      return;
    }

    const { action, date, time } = this.scheduleForm.value as {
      action: 'UPALI' | 'UGASI' | 'RESTARTUJ';
      date: string;
      time: string;
    };

    // ✅ šaljemo backend akciju: START/STOP/RESTART
    const schedule: MachineSchedule = {
      date,
      time,
      operation: this.actionMap[action],
    };

    this.submitting = true;

    this.machineService.scheduleMachine(this.machineId, schedule).subscribe({
      next: () => {
        this.submitting = false;
        this.router.navigate(['/machines']);
      },
      error: (err) => {
        this.submitting = false;
        this.errorMsg =
          err?.error?.message ||
          err?.message ||
          'Greška pri zakazivanju. Proveri permission/JWT i backend log.';
      },
    });
  }
}
