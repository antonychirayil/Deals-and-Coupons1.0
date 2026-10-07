import { Component, inject, input, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs';

import { Coupon } from '../../../core/models/coupon';
import { Page } from '../../../core/models/page';
import { AuthService } from '../../../core/services/auth-service';
import { CouponService } from '../../../core/services/coupon-service';
import { SavedCouponService } from '../../../core/services/saved-coupon-service';
import { getErrorMessage } from '../../../core/utils/error-message';
import { CouponCard } from '../../../shared/coupon-card/coupon-card';
import { categoryIcon } from '../../../shared/utils/display';

@Component({
  selector: 'app-coupon-list',
  imports: [
    CouponCard,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatPaginatorModule,
  ],
  templateUrl: './coupon-list.html',
  styleUrl: './coupon-list.css',
})
export class CouponList implements OnInit {
  private readonly couponService = inject(CouponService);
  private readonly savedCouponService = inject(SavedCouponService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  // Optional starting filters from the URL, e.g. /coupons?category=Food (links on the home page)
  readonly searchParam = input<string>(undefined, { alias: 'search' });
  readonly categoryParam = input<string>(undefined, { alias: 'category' });

  protected readonly sortOptions = [
    { value: 'expiryDate,asc', label: 'Ending soon' },
    { value: 'discount,desc', label: 'Biggest discount' },
    { value: 'provider,asc', label: 'Store A–Z' },
  ];
  protected readonly categoryIcon = categoryIcon; // so the template can call it

  // ----- Filters: what the user picked. Every change asks the backend for a new page. -----
  protected readonly searchText = signal('');
  protected readonly category = signal<string | null>(null);
  protected readonly sort = signal('expiryDate,asc');
  protected readonly showExpired = signal(false);
  protected readonly pageIndex = signal(0);
  protected readonly pageSize = signal(12);

  // ----- What came back -----
  protected readonly result = signal<Page<Coupon> | null>(null);
  protected readonly categories = signal<string[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly savedIds = signal(new Set<string>());

  private searchTimer?: ReturnType<typeof setTimeout>;
  private currentRequest?: Subscription;

  ngOnInit(): void {
    this.searchText.set(this.searchParam() ?? '');
    this.category.set(this.categoryParam() ?? null);

    this.loadCoupons();
    this.couponService.getCategories().subscribe((names) => this.categories.set(names));
    if (this.authService.isLoggedIn()) {
      this.loadSavedIds();
    }
  }

  // ----- Event handlers: update a filter, go back to page 1, reload -----

  protected onSearchInput(text: string): void {
    this.searchText.set(text);
    // "Debounce": wait until the user stops typing for 300 ms, instead of one request per key
    clearTimeout(this.searchTimer);
    this.searchTimer = setTimeout(() => this.reloadFromFirstPage(), 300);
  }

  protected clearSearch(): void {
    this.searchText.set('');
    this.reloadFromFirstPage();
  }

  protected onCategoryChange(category: string | null): void {
    this.category.set(category);
    this.reloadFromFirstPage();
  }

  protected onSortChange(sort: string): void {
    this.sort.set(sort);
    this.reloadFromFirstPage();
  }

  protected onShowExpiredChange(show: boolean): void {
    this.showExpired.set(show);
    this.reloadFromFirstPage();
  }

  // The paginator tells us which page (and page size) the user picked
  protected onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadCoupons();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  protected resetFilters(): void {
    this.searchText.set('');
    this.category.set(null);
    this.showExpired.set(false);
    this.reloadFromFirstPage();
  }

  protected loadCoupons(): void {
    // If an older request is still running (fast typing), cancel it so its answer can't overwrite a newer one
    this.currentRequest?.unsubscribe();
    this.loading.set(true);
    this.errorMessage.set(null);

    this.currentRequest = this.couponService
      .searchCoupons({
        search: this.searchText(),
        category: this.category(),
        includeExpired: this.showExpired(),
        sort: this.sort(),
        page: this.pageIndex(),
        size: this.pageSize(),
      })
      .subscribe({
        next: (page) => {
          this.result.set(page);
          this.loading.set(false);
        },
        error: (error) => {
          this.errorMessage.set(getErrorMessage(error));
          this.loading.set(false);
        },
      });
  }

  // ----- Saving -----

  protected toggleSave(coupon: Coupon): void {
    // Guests can't save: send them to log in, then bring them back here
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/coupons' } });
      return;
    }

    const isSaved = this.savedIds().has(coupon.id);
    // Observable<unknown>: we only care THAT it finished, not what it returned
    const request: Observable<unknown> = isSaved
      ? this.savedCouponService.removeSavedCoupon(coupon.id)
      : this.savedCouponService.saveCoupon(coupon.id);

    request.subscribe({
      next: () => {
        this.markSaved(coupon.id, !isSaved);
        const message = isSaved ? 'Removed from saved coupons' : `${coupon.provider} coupon saved`;
        this.snackBar.open(message, undefined, { duration: 2500 });
      },
      error: (error) =>
        this.snackBar.open(getErrorMessage(error), 'Close', { duration: 5000, panelClass: 'snackbar-error' }),
    });
  }

  private reloadFromFirstPage(): void {
    this.pageIndex.set(0);
    this.loadCoupons();
  }

  private loadSavedIds(): void {
    this.savedCouponService.getSavedCoupons().subscribe({
      next: (saved) => this.savedIds.set(new Set(saved.map((item) => item.coupon.id))),
      error: () => this.savedIds.set(new Set()), // not critical: the bookmarks just start empty
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
}
