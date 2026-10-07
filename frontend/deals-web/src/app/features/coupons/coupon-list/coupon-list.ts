import { Component, computed, inject, OnInit, signal } from '@angular/core';

import { Coupon } from '../../../core/models/coupon';
import { CouponService } from '../../../core/services/coupon-service';
import { CouponCard } from '../../../shared/coupon-card/coupon-card';

@Component({
  selector: 'app-coupon-list',
  imports: [CouponCard],
  templateUrl: './coupon-list.html',
  styleUrl: './coupon-list.css',
})
export class CouponList implements OnInit {
  private readonly couponService = inject(CouponService);

  // ----- State: plain signals we set ourselves -----
  protected readonly coupons = signal<Coupon[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly searchText = signal('');
  protected readonly selectedCategory = signal('All');

  // ----- Derived state: computed() recalculates automatically when the signals it reads change -----
  protected readonly categories = computed(() => {
    const unique = new Set(this.coupons().map((coupon) => coupon.category));
    return ['All', ...unique];
  });

  protected readonly filteredCoupons = computed(() => {
    const text = this.searchText().trim().toLowerCase();
    const category = this.selectedCategory();

    return this.coupons().filter((coupon) => {
      const matchesCategory = category === 'All' || coupon.category === category;
      const matchesText =
        text === '' ||
        coupon.provider.toLowerCase().includes(text) ||
        (coupon.description ?? '').toLowerCase().includes(text);
      return matchesCategory && matchesText;
    });
  });

  // Runs once when the component appears on screen
  ngOnInit(): void {
    this.loadCoupons();
  }

  protected loadCoupons(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    // subscribe() actually sends the request; next/error run when the answer arrives
    this.couponService.getCoupons().subscribe({
      next: (coupons) => {
        this.coupons.set(coupons);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Could not load coupons. Is the backend running?');
        this.loading.set(false);
      },
    });
  }
}
