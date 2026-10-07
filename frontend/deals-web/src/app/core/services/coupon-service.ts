import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Coupon } from '../models/coupon';

/**
 * All HTTP calls about coupons live here, so components never build URLs themselves.
 * @Service() makes Angular create ONE instance and hand it to whoever asks with inject().
 */
@Service()
export class CouponService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/coupons`;

  // Returns an Observable: the request is only sent when someone subscribes to it
  getCoupons(): Observable<Coupon[]> {
    return this.http.get<Coupon[]>(this.baseUrl);
  }
}
