import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { Observable } from 'rxjs';
import { map, first } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class PermissionGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const requiredPermissions = next.data['permissions'] as string[];

    return this.authService.authUser$.pipe(
      first(),
      map((user) => {
        console.log('%c--- DEBUG: PermissionGuard ---', 'color: orange; font-weight: bold;');
        console.log('Pokušaj pristupa na rutu:', state.url);
        console.log('Potrebne permisije za ovu stranicu:', requiredPermissions);

        if (!user) {
          console.error('PermissionGuard: Korisnik uopšte nije ulogovan (user je null)!');
          this.router.navigate(['/login']);
          return false;
        }

        console.log('Trenutni korisnik:', user.email);
        console.log('Permisije koje korisnik IMA u tokenu:', user.permissions);

        if (!user.permissions || user.permissions.length === 0) {
          console.warn('PermissionGuard: Korisnik nema NIJEDNU permisiju u nizu!');
          this.router.navigate(['/no-permissions']);
          return false;
        }

        const hasRequiredPermissions = requiredPermissions.every((permission) => {
          const exists = user.permissions.includes(permission);
          if (!exists) {
            console.error(`PermissionGuard: NEDOSTAJE PERMISIJA -> ${permission}`);
          } else {
            console.log(`PermissionGuard: Check OK za -> ${permission}`);
          }
          return exists;
        });

        console.log('Konačni rezultat provere:', hasRequiredPermissions);
        console.log('%c------------------------------', 'color: orange; font-weight: bold;');

        if (hasRequiredPermissions) {
          return true;
        } else {
          this.router.navigate(['/no-permissions']);
          return false;
        }
      })
    );
  }
}
