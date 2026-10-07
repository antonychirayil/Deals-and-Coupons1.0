import { Routes } from '@angular/router';

import { adminGuard } from './core/guards/admin-guard';
import { authGuard } from './core/guards/auth-guard';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { CouponList } from './features/coupons/coupon-list/coupon-list';
import { Home } from './features/home/home';
import { NotFound } from './features/not-found/not-found';
import { SavedCoupons } from './features/saved-coupons/saved-coupons';

// URL -> which component to show inside <router-outlet>. Checked top to bottom.
export const routes: Routes = [
  { path: '', component: Home, title: 'Deals & Coupons' },
  { path: 'coupons', component: CouponList, title: 'Browse coupons | Deals & Coupons' },
  { path: 'login', component: Login, title: 'Log in | Deals & Coupons' },
  { path: 'register', component: Register, title: 'Sign up | Deals & Coupons' },

  // canActivate: the guard runs first and decides if the page may open
  { path: 'saved', component: SavedCoupons, canActivate: [authGuard], title: 'Saved coupons | Deals & Coupons' },

  // Admin pages are LAZY-LOADED: their code is downloaded only when an admin opens them,
  // so normal visitors never download it at all.
  {
    path: 'admin',
    canActivate: [adminGuard],
    title: 'Admin | Deals & Coupons',
    loadComponent: () => import('./features/admin/admin-dashboard/admin-dashboard').then((m) => m.AdminDashboard),
  },
  {
    path: 'admin/coupons/new',
    canActivate: [adminGuard],
    title: 'New coupon | Deals & Coupons',
    loadComponent: () => import('./features/admin/coupon-form/coupon-form').then((m) => m.CouponForm),
  },
  {
    path: 'admin/coupons/:id/edit', // ":id" is a placeholder: /admin/coupons/abc123/edit
    canActivate: [adminGuard],
    title: 'Edit coupon | Deals & Coupons',
    loadComponent: () => import('./features/admin/coupon-form/coupon-form').then((m) => m.CouponForm),
  },

  { path: '**', component: NotFound, title: 'Page not found | Deals & Coupons' }, // ** = anything else
];
