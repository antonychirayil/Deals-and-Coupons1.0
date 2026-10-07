import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { SavedCoupon } from '../models/saved-coupon';

// Calls user-service (through the gateway). Every call needs a token: the interceptor adds it.
@Service()
export class SavedCouponService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/users/me/saved-coupons`;

  getSavedCoupons(): Observable<SavedCoupon[]> {
    return this.http.get<SavedCoupon[]>(this.baseUrl);
  }

  saveCoupon(couponId: string): Observable<SavedCoupon> {
    return this.http.post<SavedCoupon>(`${this.baseUrl}/${couponId}`, null);
  }

  removeSavedCoupon(couponId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${couponId}`);
  }
}
