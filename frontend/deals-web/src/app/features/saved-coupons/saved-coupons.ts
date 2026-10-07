import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Coupon } from '../../core/models/coupon';
import { SavedCoupon } from '../../core/models/saved-coupon';
import { SavedCouponService } from '../../core/services/saved-coupon-service';
import { getErrorMessage } from '../../core/utils/error-message';
import { CouponCard } from '../../shared/coupon-card/coupon-card';

// "My saved coupons" - only reachable when logged in (authGuard in app.routes.ts)
@Component({
  selector: 'app-saved-coupons',
  imports: [CouponCard, RouterLink],
  templateUrl: './saved-coupons.html',
  styleUrl: './saved-coupons.css',
})
export class SavedCoupons implements OnInit {
  private readonly savedCouponService = inject(SavedCouponService);

  protected readonly savedCoupons = signal<SavedCoupon[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.savedCouponService.getSavedCoupons().subscribe({
      next: (saved) => {
        this.savedCoupons.set(saved);
        this.loading.set(false);
      },
      error: (error) => {
        this.errorMessage.set(getErrorMessage(error));
        this.loading.set(false);
      },
    });
  }

  // On this page every star is "saved", so clicking it always means "remove"
  protected remove(coupon: Coupon): void {
    this.savedCouponService.removeSavedCoupon(coupon.id).subscribe({
      // update() = set a new value based on the current one
      next: () => this.savedCoupons.update((list) => list.filter((item) => item.coupon.id !== coupon.id)),
      error: (error) => this.errorMessage.set(getErrorMessage(error)),
    });
  }
}
