import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

import { Coupon } from '../../../core/models/coupon';
import { AuthService } from '../../../core/services/auth-service';
import { CouponService } from '../../../core/services/coupon-service';
import { SavedCouponService } from '../../../core/services/saved-coupon-service';
import { getErrorMessage } from '../../../core/utils/error-message';
import { CouponCard } from '../../../shared/coupon-card/coupon-card';

@Component({
  selector: 'app-coupon-list',
  imports: [CouponCard],
  templateUrl: './coupon-list.html',
  styleUrl: './coupon-list.css',
})
export class CouponList implements OnInit {
  private readonly couponService = inject(CouponService);
  private readonly savedCouponService = inject(SavedCouponService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  // ----- State: plain signals we set ourselves -----
  protected readonly coupons = signal<Coupon[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly searchText = signal('');
  protected readonly selectedCategory = signal('All');
  protected readonly savedIds = signal(new Set<string>()); // ids of coupons this user saved
  protected readonly saveError = signal<string | null>(null);

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
    if (this.authService.isLoggedIn()) {
      this.loadSavedIds();
    }
  }

  protected toggleSave(coupon: Coupon): void {
    // Guests can't save: send them to log in, then bring them back here
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/coupons' } });
      return;
    }

    this.saveError.set(null);
    const isSaved = this.savedIds().has(coupon.id);
    // Observable<unknown>: we only care THAT it finished, not what it returned
    const request: Observable<unknown> = isSaved
      ? this.savedCouponService.removeSavedCoupon(coupon.id)
      : this.savedCouponService.saveCoupon(coupon.id);

    request.subscribe({
      next: () => this.markSaved(coupon.id, !isSaved),
      error: (error) => this.saveError.set(getErrorMessage(error)),
    });
  }

  private loadSavedIds(): void {
    this.savedCouponService.getSavedCoupons().subscribe({
      next: (saved) => this.savedIds.set(new Set(saved.map((item) => item.coupon.id))),
      error: () => this.savedIds.set(new Set()), // not critical: the stars just start empty
    });
  }

  private markSaved(couponId: string, saved: boolean): void {
    // Make a NEW Set: a signal only notices a change when it gets a different object
    const ids = new Set(this.savedIds());
    if (saved) {
      ids.add(couponId);
    } else {
      ids.delete(couponId);
    }
    this.savedIds.set(ids);
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
