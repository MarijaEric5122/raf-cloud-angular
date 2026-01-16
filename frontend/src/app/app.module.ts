import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { UsersListComponent } from './components/users-list/users-list.component';
import { NoPermissionsComponent } from './components/no-permissions/no-permissions.component';
import { UsersFormComponent } from './components/users-form/users-form.component';
import { MachinesSearchComponent } from './components/machines-search/machines-search.component';
import { MachinesCreateComponent } from './components/machines-create/machines-create.component';
import { MachinesErrorLogsListComponent } from './components/machines-error-logs-list/machines-error-logs-list.component';
import { MachinesScheduleComponent } from './components/machines-schedule/machines-schedule.component';
import { MachinesScheduleListComponent } from './components/machines-schedule-list/machines-schedule-list.component';

import { HttpClientModule } from '@angular/common/http';
import { HomeComponent } from './components/home/home.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    NoPermissionsComponent,
    UsersListComponent,
    UsersFormComponent,
    HomeComponent,
    MachinesSearchComponent,
    MachinesCreateComponent,
    MachinesErrorLogsListComponent,
    MachinesScheduleComponent,
    MachinesScheduleListComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
