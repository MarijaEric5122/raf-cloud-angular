import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { UsersListComponent } from './components/users-list/users-list.component';
import { AuthGuard } from './guards/auth.guard';
import { PermissionGuard } from './guards/permission.guard';
import { NoPermissionsComponent } from './components/no-permissions/no-permissions.component';
import { UsersFormComponent } from './components/users-form/users-form.component';
import { MachinesSearchComponent } from './components/machines-search/machines-search.component';
import { MachinesCreateComponent } from './components/machines-create/machines-create.component';
import { MachinesErrorLogsListComponent } from './components/machines-error-logs-list/machines-error-logs-list.component';
import { MachinesScheduleComponent } from './components/machines-schedule/machines-schedule.component';
import { MachinesScheduleListComponent } from './components/machines-schedule-list/machines-schedule-list.component';
import { HomeComponent } from './components/home/home.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'no-permissions', component: NoPermissionsComponent },
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      { path: 'home', component: HomeComponent },
      {
        path: 'users',
        component: UsersListComponent,
        canActivate: [PermissionGuard],
        data: { permissions: ['CAN_READ_USERS'] },
      },
      {
        path: 'users/new',
        component: UsersFormComponent,
        canActivate: [PermissionGuard],
        data: { permissions: ['CAN_CREATE_USERS'] },
      },
      {
        path: 'users/:id',
        component: UsersFormComponent,
        canActivate: [PermissionGuard],
        data: { permissions: ['CAN_UPDATE_USERS'] },
      },

      {
        path: 'machines',
        component: MachinesSearchComponent,
        canActivate: [PermissionGuard],
        data: { permissions: ['CAN_SEARCH_MACHINES'] },
      },
      {
        path: 'machines/new',
        component: MachinesCreateComponent,
        canActivate: [PermissionGuard],
        data: { permissions: ['CAN_CREATE_MACHINES'] },
      },
      {
        path: 'machines/schedule/:id',
        component: MachinesScheduleComponent,
        canActivate: [PermissionGuard],
        data: { permissions: ['CAN_READ_SCHEDULES'] },
      },

      {
        path: 'errors',
        component: MachinesErrorLogsListComponent,
        canActivate: [PermissionGuard],
        data: { permissions: ['CAN_READ_ERRORS'] },
      },
      {
        path: 'machines/schedules',
        component: MachinesScheduleListComponent,
        canActivate: [PermissionGuard],
        data: { permissions: ['CAN_READ_SCHEDULES'] },
      },

      { path: '', redirectTo: '/home', pathMatch: 'full' },
    ],
  },
  { path: '**', redirectTo: '/login' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
