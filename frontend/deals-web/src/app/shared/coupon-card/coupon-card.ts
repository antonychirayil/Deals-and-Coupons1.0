import { CdkCopyToClipboard } from '@angular/cdk/clipboard';
import { Component, computed, inject, input, output, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router } from '@angular/router';

import { Coupon } from '../../core/models/coupon';
import { ExpiryLabelPipe } from '../pipes/expiry-label-pipe';
import { categoryIcon, daysUntil, storeColor, storeInitials } from '../utils/display';

/**
 * Shows ONE coupon. Data comes IN through inputs, events go OUT through outputs:
 *   <app-coupon-card [coupon]="c" [saved]="true" (saveToggled)="onToggle($event)" />
 */
@Component({
  selector: 'app-coupon-card',
  imports: [MatCardModule, MatButtonModule, MatIconModule, MatTooltipModule, CdkCopyToClipboard, ExpiryLabelPipe],
  templateUrl: './coupon-card.html',
  styleUrl: './coupon-card.css',
})
export class CouponCard {
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  // input.required = the parent MUST give us a coupon. Read it like a function: coupon()
  readonly coupon = input.required<Coupon>();
  readonly saved = input(false); // optional input with a default value
  readonly showSave = input(true); // the home page hides the bookmark button

  // An output is an event the parent can listen to. The card doesn't know what "save" means;
  // it just announces that the button was clicked.
  readonly saveToggled = output<Coupon>();

  protected readonly codeVisible = signal(false);

  // The backend leaves the code out (null) when nobody is logged in
  protected readonly needsLogin = computed(() => this.coupon().code === null);

  // Values derived from the coupon. computed() recalculates only when coupon() changes.
  protected readonly initials = computed(() => storeInitials(this.coupon().provider));
  protected readonly avatarColor = computed(() => storeColor(this.coupon().provider));
  protected readonly icon = computed(() => categoryIcon(this.coupon().category));
  protected readonly endingSoon = computed(() => {
    const days = daysUntil(this.coupon().expiryDate);
    return days >= 0 && days <= 3;
  });

  protected showCode(): void {
    this.codeVisible.set(true);
  }

  // Send the guest to log in, then bring them back to the page they were on
  protected logInToSeeCode(): void {
    this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
  }

  protected toggleSave(): void {
    this.saveToggled.emit(this.coupon());
  }

  // cdkCopyToClipboard tells us whether copying worked (some browsers block it)
  protected onCopied(success: boolean): void {
    const message = success ? `Code ${this.coupon().code} copied!` : 'Could not copy - please select the code';
    this.snackBar.open(message, undefined, { duration: 2500 });
  }
}
