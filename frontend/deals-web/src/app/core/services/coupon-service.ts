import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Coupon, CouponRequest } from '../models/coupon';
import { Page } from '../models/page';

// Everything is optional: leave a value out and the backend uses its default
export interface CouponQuery {
  search?: string;
  category?: string | null;
  includeExpired?: boolean;
  page?: number;
  size?: number;
  sort?: string; // "field,direction", e.g. "discount,desc"
}

/**
 * All HTTP calls about coupons live here, so components never build URLs themselves.
 * @Service() makes Angular create ONE instance and hand it to whoever asks with inject().
 */
@Service()
export class CouponService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/coupons`;

  // GET /api/coupons?search=..&category=..&page=0&size=12&sort=expiryDate,asc
  searchCoupons(query: CouponQuery = {}): Observable<Page<Coupon>> {
    // HttpParams builds the "?a=1&b=2" part. Only filled-in values are added.
    let params = new HttpParams();
    if (query.search?.trim()) params = params.set('search', query.search.trim());
    if (query.category) params = params.set('category', query.category);
    if (query.includeExpired) params = params.set('includeExpired', true);
    if (query.page !== undefined) params = params.set('page', query.page);
    if (query.size !== undefined) params = params.set('size', query.size);
    if (query.sort) params = params.set('sort', query.sort);

    return this.http.get<Page<Coupon>>(this.baseUrl, { params });
  }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/categories`);
  }

  getCoupon(id: string): Observable<Coupon> {
    return this.http.get<Coupon>(`${this.baseUrl}/${id}`);
  }

  // The three below need an ADMIN token; the interceptor adds it automatically
  createCoupon(request: CouponRequest): Observable<Coupon> {
    return this.http.post<Coupon>(this.baseUrl, request);
  }

  updateCoupon(id: string, request: CouponRequest): Observable<Coupon> {
    return this.http.put<Coupon>(`${this.baseUrl}/${id}`, request);
  }

  deleteCoupon(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
