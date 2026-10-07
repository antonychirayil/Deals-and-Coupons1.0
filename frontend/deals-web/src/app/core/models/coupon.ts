// The shape of a coupon as the backend sends it (matches CouponResponse in coupon-service).
// An interface only describes data: it has no code and disappears when compiled to JavaScript.
export interface Coupon {
  id: string;
  code: string;
  provider: string;
  category: string;
  description: string | null; // "| null" = this field may be missing
  discount: number;
  expiryDate: string; // "2027-12-31"
  expired: boolean;
}
