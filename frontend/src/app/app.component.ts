import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { map, Observable } from 'rxjs';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'proba';
  isLoggedIn$: Observable<boolean>;
  canReadUsers$: Observable<boolean>;
  canSearchMachines$: Observable<boolean>;
  canReadErrors$: Observable<boolean>;
  canReadSchedules$: Observable<boolean>;

  constructor(private authService: AuthService, private router: Router) {
    this.isLoggedIn$ = this.authService.authUser$.pipe(map((user) => !!user));
    this.canReadUsers$ = this.authService.hasPermission('CAN_READ_USERS');
    this.canSearchMachines$ = this.authService.hasPermission(
      'CAN_SEARCH_MACHINES'
    );
    this.canReadErrors$ = this.authService.hasPermission('CAN_READ_ERRORS');
    this.canReadSchedules$ =
      this.authService.hasPermission('CAN_READ_SCHEDULES');
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
