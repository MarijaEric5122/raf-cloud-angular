import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap, map } from 'rxjs';
import { HttpClient } from '@angular/common/http'; // DODAJ OVO
import { User } from '../models/user.model';

const KEY_AUTH_USER = 'authUser';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private _authUserSubject: BehaviorSubject<any | null>; // Promenjeno u any zbog JWT-a
  public authUser$: Observable<any | null>;

  // URL tvog Spring Boot-a
  private readonly apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) { // Ubrizgaj HttpClient
    const storedUser = localStorage.getItem(KEY_AUTH_USER);
    this._authUserSubject = new BehaviorSubject<any | null>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.authUser$ = this._authUserSubject.asObservable();
  }

  login(email: string, password: string): Observable<any> {
    // PRAVI HTTP POZIV KA BEKENDU
    return this.http.post<any>(`${this.apiUrl}/login`, { email, password }).pipe(
      tap((response) => {
        // 'response' je ono što tvoj Java kod vraća (jwt, email, permissions)
        this._authUserSubject.next(response);
        localStorage.setItem(KEY_AUTH_USER, JSON.stringify(response));
        // Token se čuva da bi ostali servisi mogli da ga koriste
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
      map((user) => {
        // Provera permisija iz objekta koji je vratio backend
        return user?.permissions?.includes(permission) || false;
      })
    );
  }
}
