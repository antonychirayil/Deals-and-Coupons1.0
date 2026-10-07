import { DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSortModule, Sort } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { forkJoin, Subscription } from 'rxjs';

import { Coupon } from '../../../core/models/coupon';
import { CouponService } from '../../../core/services/coupon-service';
import { getErrorMessage } from '../../../core/utils/error-message';
import { ConfirmDialog } from '../../../shared/confirm-dialog/confirm-dialog';
import { daysUntil, storeColor, storeInitials } from '../../../shared/utils/display';

// Admin-only page (adminGuard in app.routes.ts): search, sort and page through ALL coupons
@Component({
  selector: 'app-admin-dashboard',
  imports: [
    RouterLink,
    DatePipe,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatProgressBarModule,
  ],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard implements OnInit {
  private readonly couponService = inject(CouponService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  // Which columns the table shows, in order (each one has a matColumnDef in the template)
  protected readonly columns = ['code', 'provider', 'category', 'discount', 'expiryDate', 'actions'];
  protected readonly storeColor = storeColor;
  protected readonly storeInitials = storeInitials;

  protected readonly coupons = signal<Coupon[]>([]);
  protected readonly totalElements = signal(0);
  protected readonly stats = signal<{ total: number; active: number } | null>(null);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly searchText = signal('');
  protected readonly pageIndex = signal(0);
  protected readonly pageSize = signal(20);
  protected readonly sort = signal<Sort>({ active: 'provider', direction: 'asc' });

  private searchTimer?: ReturnType<typeof setTimeout>;
  private currentRequest?: Subscription;

  ngOnInit(): void {
    this.loadCoupons();
    this.loadStats();
  }

  protected onSearchInput(text: string): void {
    this.searchText.set(text);
    clearTimeout(this.searchTimer);
    this.searchTimer = setTimeout(() => {
      this.pageIndex.set(0);
      this.loadCoupons();
    }, 300);
  }

  // Clicking a column header: the BACKEND sorts all coupons, not just the 20 on screen
  protected onSortChange(sort: Sort): void {
    this.sort.set(sort);
    this.pageIndex.set(0);
    this.loadCoupons();
  }

  protected onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadCoupons();
  }

  protected status(coupon: Coupon): 'expired' | 'soon' | 'active' {
    if (coupon.expired) return 'expired';
    return daysUntil(coupon.expiryDate) <= 3 ? 'soon' : 'active';
  }

  protected confirmDelete(coupon: Coupon): void {
    this.dialog
      .open(ConfirmDialog, {
        width: '420px',
        data: {
          title: 'Delete coupon?',
          message: `${coupon.code} from ${coupon.provider} will be removed for everyone. This can't be undone.`,
          confirmText: 'Delete',
        },
      })
      .afterClosed() // an Observable that emits once, when the dialog closes
      .subscribe((confirmed) => {
        if (confirmed) {
          this.deleteCoupon(coupon);
        }
      });
  }

  protected loadCoupons(): void {
    this.currentRequest?.unsubscribe();
    this.loading.set(true);
    this.errorMessage.set(null);

    const { active, direction } = this.sort();
    this.currentRequest = this.couponService
      .searchCoupons({
        search: this.searchText(),
        includeExpired: true, // admins see everything
        page: this.pageIndex(),
        size: this.pageSize(),
        sort: direction ? `${active},${direction}` : 'provider,asc',
      })
      .subscribe({
        next: (page) => {
          this.coupons.set(page.content);
          this.totalElements.set(page.totalElements);
          this.loading.set(false);
        },
        error: (error) => {
          this.errorMessage.set(getErrorMessage(error));
          this.loading.set(false);
        },
      });
  }

  private deleteCoupon(coupon: Coupon): void {
    this.couponService.deleteCoupon(coupon.id).subscribe({
      next: () => {
        this.snackBar.open(`Coupon ${coupon.code} deleted`, undefined, { duration: 3000 });
        this.loadCoupons(); // reload so the page is refilled from the next one
        this.loadStats();
      },
      error: (error) =>
        this.snackBar.open(getErrorMessage(error), 'Close', { duration: 5000, panelClass: 'snackbar-error' }),
    });
  }

  // Two tiny requests (size 1) just to read their totalElements. forkJoin waits for BOTH to finish.
  private loadStats(): void {
    forkJoin({
      all: this.couponService.searchCoupons({ includeExpired: true, size: 1 }),
      active: this.couponService.searchCoupons({ size: 1 }),
    }).subscribe({
      next: ({ all, active }) => this.stats.set({ total: all.totalElements, active: active.totalElements }),
      error: () => this.stats.set(null),
    });
  }
}
