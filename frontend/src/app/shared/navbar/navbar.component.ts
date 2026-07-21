import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { NotificationBellComponent } from '../notifications/notification-bell.component';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive, NotificationBellComponent],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {
  private auth = inject(AuthService);

  readonly user = this.auth.currentUser;
  readonly isAdmin = this.auth.isAdmin;

  logout(): void {
    this.auth.logout();
  }
}
