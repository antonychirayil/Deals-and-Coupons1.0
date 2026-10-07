import { DatePipe } from '@angular/common';
import { Component, input, signal } from '@angular/core';

import { Coupon } from '../../core/models/coupon';

/**
 * Shows ONE coupon. The parent passes the coupon in:  <app-coupon-card [coupon]="c" />
 */
@Component({
  selector: 'app-coupon-card',
  imports: [DatePipe],
  templateUrl: './coupon-card.html',
  styleUrl: './coupon-card.css',
})
export class CouponCard {
  // input.required = the parent MUST give us a coupon. Read it like a function: coupon()
  readonly coupon = input.required<Coupon>();

  // A signal is a value that Angular watches: when it changes, the template updates
  protected readonly codeVisible = signal(false);

  protected showCode(): void {
    this.codeVisible.set(true);
  }
}
