import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { AuthService } from '../../core/services/auth-service';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  private readonly router = inject(Router);

  // The template reads authService.isLoggedIn() etc. Because they're signals,
  // the navbar updates by itself the moment someone logs in or out.
  protected readonly authService = inject(AuthService);

  protected logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/');
  }
}
