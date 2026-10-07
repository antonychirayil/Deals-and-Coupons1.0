import { Component, computed, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { AuthService } from '../../core/services/auth-service';
import { storeInitials } from '../utils/display';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive, MatToolbarModule, MatButtonModule, MatIconModule, MatMenuModule, MatDividerModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  private readonly router = inject(Router);

  // The template reads authService.isLoggedIn() etc. Because they're signals,
  // the navbar updates by itself the moment someone logs in or out.
  protected readonly authService = inject(AuthService);

  protected readonly userInitials = computed(() => storeInitials(this.authService.currentUser()?.name ?? '?'));

  protected logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/');
  }
}
