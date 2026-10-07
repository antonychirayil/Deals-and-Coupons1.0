// One page of results from the backend (matches PageResponse in coupon-service).
// <T> means "a page of anything": Page<Coupon>, Page<User>, ...
export interface Page<T> {
  content: T[];
  page: number; // 0 = first page
  size: number;
  totalElements: number;
  totalPages: number;
}
