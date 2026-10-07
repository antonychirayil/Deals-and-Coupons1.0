import { Coupon } from './coupon';

// One entry in "my saved coupons" (matches SavedCouponResponse in user-service)
export interface SavedCoupon {
  coupon: Coupon;
  savedAt: string;
}
