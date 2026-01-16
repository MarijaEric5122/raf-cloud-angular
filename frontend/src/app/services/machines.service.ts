import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Machine,
  MachineSchedule,
  MachineState,
  MachineScheduleRow,
} from '../models/machine.model';

@Injectable({ providedIn: 'root' })
export class MachinesService {
  private baseUrl = 'http://localhost:8080/api/machines';

  constructor(private http: HttpClient) {}

  private authHeaders(): HttpHeaders {
    const token =
      localStorage.getItem('token') ||
      localStorage.getItem('jwt') ||
      localStorage.getItem('accessToken');

    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (token) headers = headers.set('Authorization', `Bearer ${token}`);
    return headers;
  }

  search(filter: {
    name?: string;
    states?: MachineState[];
    startDate?: string;
    endDate?: string;
  }): Observable<Machine[]> {
    let params = new HttpParams();

    if (filter.name?.trim()) params = params.set('name', filter.name.trim());
    if (filter.startDate) params = params.set('startDate', filter.startDate);
    if (filter.endDate) params = params.set('endDate', filter.endDate);

    if (filter.states?.length) {
      filter.states.forEach((s) => (params = params.append('states', s)));
    }

    return this.http.get<Machine[]>(this.baseUrl, {
      headers: this.authHeaders(),
      params,
    });
  }

  create(machine: {
    name: string;
    type: string;
    description?: string;
  }): Observable<Machine> {
    const body = {
      name: machine.name,
      type: machine.type,
      description: machine.description ?? null,
    };

    return this.http.post<Machine>(this.baseUrl, body, {
      headers: this.authHeaders(),
    });
  }

  destroy(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, {
      headers: this.authHeaders(),
    });
  }

  startMachine(id: number): Observable<void> {
    return this.http.post<void>(
      `${this.baseUrl}/${id}/start`,
      {},
      { headers: this.authHeaders() }
    );
  }

  stopMachine(id: number): Observable<void> {
    return this.http.post<void>(
      `${this.baseUrl}/${id}/stop`,
      {},
      { headers: this.authHeaders() }
    );
  }

  restartMachine(id: number): Observable<void> {
    return this.http.post<void>(
      `${this.baseUrl}/${id}/restart`,
      {},
      { headers: this.authHeaders() }
    );
  }

  scheduleMachine(
    machineId: number,
    schedule: MachineSchedule
  ): Observable<void> {
    const body = {
      action: schedule.operation,
      date: schedule.date,
      time: schedule.time,
    };

    return this.http.post<void>(`${this.baseUrl}/${machineId}/schedule`, body, {
      headers: this.authHeaders(),
    });
  }

  getSchedules(): Observable<MachineScheduleRow[]> {
    return this.http.get<MachineScheduleRow[]>(
      'http://localhost:8080/api/machine-schedule',
      { headers: this.authHeaders() }
    );
  }
}
