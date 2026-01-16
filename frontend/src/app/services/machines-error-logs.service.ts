import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MachinesErrorLog } from '../models/machine.model';

@Injectable({ providedIn: 'root' })
export class MachinesErrorLogsService {
  private baseUrl = 'http://localhost:8080/api/machine-error-logs';

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

  getAllVisible(): Observable<MachinesErrorLog[]> {
    return this.http.get<MachinesErrorLog[]>(this.baseUrl, {
      headers: this.authHeaders(),
    });
  }
}
