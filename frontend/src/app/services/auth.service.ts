import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap, map } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/user.model';

const KEY_AUTH_USER = 'authUser';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private _authUserSubject: BehaviorSubject<any | null>;
  public authUser$: Observable<any | null>;

  private readonly apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {
    const storedUser = localStorage.getItem(KEY_AUTH_USER);
    this._authUserSubject = new BehaviorSubject<any | null>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.authUser$ = this._authUserSubject.asObservable();
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, { email, password }).pipe(
      tap((response) => {
        this._authUserSubject.next(response);
        localStorage.setItem(KEY_AUTH_USER, JSON.stringify(response));
        localStorage.setItem('token', response.jwt);
      })
    );
  }

  logout(): void {
    this._authUserSubject.next(null);
    localStorage.removeItem(KEY_AUTH_USER);
    localStorage.removeItem('token');
  }

  hasPermission(permission: string): Observable<boolean> {
    return this.authUser$.pipe(
      map((user) => user?.permissions?.includes(permission) || false)
    );
  }
}
