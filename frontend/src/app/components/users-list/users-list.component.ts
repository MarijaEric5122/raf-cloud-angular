import { Component, OnInit } from '@angular/core';
import { User } from '../../models/user.model';
import { UserService } from '../../services/users.service';
import { Router } from '@angular/router';
import { first, switchMap } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
})
export class UsersListComponent implements OnInit {
  users: User[] = [];

  canCreate = false;
  canUpdate = false;
  canDelete = false;

  constructor(
    private usersService: UserService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.usersService
      .getUsers()
      .pipe(first())
      .subscribe((users) => {
        this.users = users;
      });

    forkJoin({
      canCreate: this.authService.hasPermission('CAN_CREATE_USERS').pipe(first()),
      canUpdate: this.authService.hasPermission('CAN_UPDATE_USERS').pipe(first()),
      canDelete: this.authService.hasPermission('CAN_DELETE_USERS').pipe(first()),
    }).subscribe((permissions) => {
      this.canCreate = permissions.canCreate;
      this.canUpdate = permissions.canUpdate;
      this.canDelete = permissions.canDelete;
    });
  }

  deleteUser(id: string) {
    if (!this.canDelete) {
      alert('Nemaš dozvolu za brisanje korisnika!');
      return;
    }
    this.usersService
      .deleteUser(id)
      .pipe(
        first(),
        switchMap(() => this.usersService.getUsers().pipe(first()))
      )
      .subscribe((users) => {
        this.users = users;
      });
  }

  goToEdit(user: User) {
    if (!this.canUpdate) {
      alert('Nemaš dozvolu za izmenu!');
      return;
    }
    this.router.navigate(['/users', user.id]);
  }

  newUser() {
    if (!this.canCreate) {
      alert('Nemaš dozvolu za pravljenje korisnika!');
      return;
    }
    this.router.navigate(['/users/new']);
  }
}
