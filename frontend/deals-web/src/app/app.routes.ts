import { Routes } from '@angular/router';

import { adminGuard } from './core/guards/admin-guard';
import { authGuard } from './core/guards/auth-guard';
import { Home } from './features/home/home';
import { NotFound } from './features/not-found/not-found';

// URL -> which component to show inside <router-outlet>. Checked top to bottom.
//
// Only Home and NotFound are loaded up front. Every other page is LAZY-LOADED:
// loadComponent downloads that page's code the first time someone opens it,
// so the first visit to the site downloads much less.
export const routes: Routes = [
  { path: '', component: Home, title: 'Deals & Coupons' },
  {
    path: 'coupons',
    title: 'Browse coupons | Deals & Coupons',
    loadComponent: () => import('./features/coupons/coupon-list/coupon-list').then((m) => m.CouponList),
  },
  {
    path: 'login',
    title: 'Log in | Deals & Coupons',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'register',
    title: 'Sign up | Deals & Coupons',
    loadComponent: () => import('./features/auth/register/register').then((m) => m.Register),
  },

  // canActivate: the guard runs first and decides if the page may open
  {
    path: 'saved',
    canActivate: [authGuard],
    title: 'Saved coupons | Deals & Coupons',
    loadComponent: () => import('./features/saved-coupons/saved-coupons').then((m) => m.SavedCoupons),
  },
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
