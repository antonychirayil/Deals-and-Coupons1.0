import { DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Coupon } from '../../../core/models/coupon';
import { CouponService } from '../../../core/services/coupon-service';
import { getErrorMessage } from '../../../core/utils/error-message';

// Admin-only page (adminGuard in app.routes.ts): list every coupon with Edit / Delete
@Component({
  selector: 'app-admin-dashboard',
  imports: [RouterLink, DatePipe],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard implements OnInit {
  private readonly couponService = inject(CouponService);

  protected readonly coupons = signal<Coupon[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.couponService.getCoupons().subscribe({
      next: (coupons) => {
        this.coupons.set(coupons);
        this.loading.set(false);
      },
      error: (error) => {
        this.errorMessage.set(getErrorMessage(error));
        this.loading.set(false);
      },
    });
  }

  protected deleteCoupon(coupon: Coupon): void {
    // confirm() shows the browser's built-in OK/Cancel dialog
    if (!confirm(`Delete coupon ${coupon.code} from ${coupon.provider}?`)) {
      return;
    }

    this.errorMessage.set(null);
    this.couponService.deleteCoupon(coupon.id).subscribe({
      next: () => this.coupons.update((list) => list.filter((item) => item.id !== coupon.id)),
      error: (error) => this.errorMessage.set(getErrorMessage(error)),
    });
  }
}
