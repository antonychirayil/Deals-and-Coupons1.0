import { Routes } from '@angular/router';

import { CouponList } from './features/coupons/coupon-list/coupon-list';
import { Home } from './features/home/home';
import { NotFound } from './features/not-found/not-found';

// URL -> which component to show inside <router-outlet>. Checked top to bottom.
export const routes: Routes = [
  { path: '', component: Home, title: 'Deals & Coupons' },
  { path: 'coupons', component: CouponList, title: 'Browse coupons | Deals & Coupons' },
  { path: '**', component: NotFound, title: 'Page not found | Deals & Coupons' }, // ** = anything else
];
