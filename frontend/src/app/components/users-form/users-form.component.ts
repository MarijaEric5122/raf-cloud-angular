import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../../models/user.model';
import { UserService } from '../../services/users.service';
import { authPermissions } from 'src/app/models/auth-permissions';
import { of, switchMap, first } from 'rxjs';

@Component({
  selector: 'app-users-form',
  templateUrl: './users-form.component.html',
  styleUrls: ['./users-form.component.css'],
})
export class UsersFormComponent implements OnInit {
  user: User = {
    id: '0',
    firstName: '',
    lastName: '',
    email: '',
    permissions: [],
    password: '',
  };

  isEdit = false;

  constructor(
    private usersService: UserService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  allPermissions = authPermissions;

  ngOnInit(): void {
    this.route.paramMap
      .pipe(
        switchMap((params) => {
          const idParam = params.get('id');
          if (idParam) {
            this.isEdit = true;
            return this.usersService.getUser(idParam);
          }
          return of(this.user);
        }),
        first()
      )
      .subscribe((user) => {
        if (user) {
          this.user = { ...user, permissions: [...user.permissions] };
        }
      });
  }

  onSubmit(): void {
    if (!this.user.firstName || !this.user.lastName || !this.user.email) {
      alert('Sva polja su obavezna!');
      return;
    }

    // Clone form model
    const userToSave = { ...this.user };

    if (!this.isEdit) {
      // Remove id for create
      // @ts-ignore
      delete userToSave.id;
    }

    const userAction = this.isEdit
      ? this.usersService.updateUser(userToSave)
      : this.usersService.createUser(userToSave);

    userAction.pipe(first()).subscribe({
      next: () => {
        this.router.navigate(['/users']);
      },
      error: (err) => {
        alert('Došlo je do greške: ' + err.status);
        console.error(err);
      },
    });
  }

  togglePermission(p: string): void {
    if (this.user.permissions.includes(p)) {
      this.user.permissions = this.user.permissions.filter((x) => x !== p);
    } else {
      this.user.permissions.push(p);
    }
  }

  cancel(): void {
    this.router.navigate(['/users']);
  }
}
