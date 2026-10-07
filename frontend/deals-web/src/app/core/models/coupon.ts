// The shape of a coupon as the backend sends it (matches CouponResponse in coupon-service).
// An interface only describes data: it has no code and disappears when compiled to JavaScript.
export interface Coupon {
  id: string;
  code: string | null; // null for guests: the backend only sends codes to logged-in users
  provider: string;
  category: string;
  description: string | null; // "| null" = this field may be missing
  discount: number;
  expiryDate: string; // "2027-12-31"
  expired: boolean;
}

// What we send to create or update a coupon (matches CouponRequest in coupon-service)
export interface CouponRequest {
  code: string | null; // null for guests: the backend only sends codes to logged-in users
  provider: string;
  category: string;
  description: string;
  discount: number;
  expiryDate: string;
}
