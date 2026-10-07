import { Component, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';

import { Coupon } from '../../core/models/coupon';
import { CouponService } from '../../core/services/coupon-service';
import { CouponCard } from '../../shared/coupon-card/coupon-card';
import { categoryIcon } from '../../shared/utils/display';

@Component({
  selector: 'app-home',
  imports: [RouterLink, MatButtonModule, MatIconModule, CouponCard],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  private readonly couponService = inject(CouponService);
  private readonly router = inject(Router);

  protected readonly topDeals = signal<Coupon[]>([]);
  protected readonly categories = signal<string[]>([]);
  protected readonly activeCount = signal<number | null>(null);
  protected readonly categoryIcon = categoryIcon;

  protected readonly steps = [
    { icon: 'travel_explore', title: 'Find', text: 'Search hundreds of coupons from your favourite stores.' },
    { icon: 'lock_open', title: 'Unlock', text: 'Sign up free (or log in) to reveal every coupon code.' },
    { icon: 'savings', title: 'Save', text: 'Paste the code at checkout and pay less.' },
  ];

  ngOnInit(): void {
    // Top deals = the 8 biggest discounts that are still valid. The same call tells us how many are active.
    this.couponService.searchCoupons({ sort: 'discount,desc', size: 8 }).subscribe({
      next: (page) => {
        this.topDeals.set(page.content);
        this.activeCount.set(page.totalElements);
      },
      error: () => this.topDeals.set([]), // the home page still works without this section
    });
    this.couponService.getCategories().subscribe((names) => this.categories.set(names));
  }

  // Hero search box -> the browse page, with the text already filled in (/coupons?search=pizza)
  protected search(event: Event, text: string): void {
    event.preventDefault(); // stop the browser's normal form submit (which would reload the page)
    const query = text.trim();
    this.router.navigate(['/coupons'], { queryParams: query ? { search: query } : {} });
  }
}
