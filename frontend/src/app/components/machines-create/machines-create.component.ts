import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { first } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { MachinesService } from '../../services/machines.service';

@Component({
  selector: 'app-machines-create',
  templateUrl: './machines-create.component.html',
  styleUrls: ['./machines-create.component.css'],
})
export class MachinesCreateComponent {
  machineForm: FormGroup;

  constructor(
    private machinesService: MachinesService,
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.machineForm = this.fb.group({
      name: ['', Validators.required],
      type: ['', Validators.required],
      description: [''],
    });
  }

  onSubmit(): void {
    if (this.machineForm.invalid) {
      alert('Naziv i tip su obavezni');
      return;
    }

    this.authService.authUser$.pipe(first()).subscribe((user) => {
      if (user) {
        this.machinesService
          .create({
            ...this.machineForm.value,
            createdBy: user.id,
          })
          .subscribe(() => {
            this.router.navigate(['/machines']);
          });
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/machines']);
  }
}
