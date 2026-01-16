import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
})
export class LoginComponent {
  email = '';
  password = '';
  errorMsg = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    this.authService
      .login(this.email, this.password)
      .pipe(first())
      .subscribe({
        next: (user) => {
          this.router.navigate(['/']);
        },
        error: (err) => {
          this.errorMsg = 'Pogrešan email ili lozinka';
        }
      });
  }
}
