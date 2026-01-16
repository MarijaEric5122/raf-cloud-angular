import {AuthService} from "../../services/auth.service";
import {Component, OnInit} from "@angular/core";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {
  email: string = '';
  hasNoPermissions: boolean = false;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.authUser$.subscribe(user => {
      this.email = user?.email || '';
      this.hasNoPermissions = !user?.permissions || user.permissions.length === 0;
    });
  }
}
