import { DatePipe } from '@angular/common';
import { Component, input, output, signal } from '@angular/core';

import { Coupon } from '../../core/models/coupon';

/**
 * Shows ONE coupon. Data comes IN through inputs, events go OUT through outputs:
 *   <app-coupon-card [coupon]="c" [saved]="true" (saveToggled)="onToggle($event)" />
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
  readonly saved = input(false); // optional input with a default value

  // An output is an event the parent can listen to. The card doesn't know what "save" means;
  // it just announces that the button was clicked.
  readonly saveToggled = output<Coupon>();

  // A signal is a value that Angular watches: when it changes, the template updates
  protected readonly codeVisible = signal(false);

  protected showCode(): void {
    this.codeVisible.set(true);
  }

  protected toggleSave(): void {
    this.saveToggled.emit(this.coupon());
  }
}
