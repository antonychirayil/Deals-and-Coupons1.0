import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { CouponRequest } from '../../../core/models/coupon';
import { CouponService } from '../../../core/services/coupon-service';
import { getErrorMessage } from '../../../core/utils/error-message';

/**
 * ONE form for both pages:
 *   /admin/coupons/new        -> id() is undefined -> create
 *   /admin/coupons/abc/edit   -> id() is "abc"     -> load, then update
 */
@Component({
  selector: 'app-coupon-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './coupon-form.html',
  styleUrl: './coupon-form.css',
})
export class CouponForm implements OnInit {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly couponService = inject(CouponService);
  private readonly router = inject(Router);

  readonly id = input<string>(); // the :id part of the URL

  protected readonly isEdit = computed(() => this.id() !== undefined);
  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  // Same rules as CouponRequest in coupon-service
  protected readonly form = this.formBuilder.group({
    code: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
    provider: ['', Validators.required],
    category: ['', Validators.required],
    description: ['', Validators.maxLength(500)],
    discount: this.formBuilder.control<number | null>(null, [
      Validators.required,
      Validators.min(0),
      Validators.max(100),
    ]),
    expiryDate: ['', Validators.required], // <input type="date"> gives "2027-12-31", the format the backend expects
  });

  ngOnInit(): void {
    const id = this.id();
    if (id) {
      this.couponService.getCoupon(id).subscribe({
        // setValue() fills every field of the form at once
        next: (coupon) =>
          this.form.setValue({
            code: coupon.code,
            provider: coupon.provider,
            category: coupon.category,
            description: coupon.description ?? '',
            discount: coupon.discount,
            expiryDate: coupon.expiryDate,
          }),
        error: (error) => this.errorMessage.set(getErrorMessage(error)),
      });
    }
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set(null);

    // The validators guarantee discount isn't null here, so it's safe to treat it as a CouponRequest
    const request = this.form.getRawValue() as CouponRequest;
    const id = this.id();
    const save = id ? this.couponService.updateCoupon(id, request) : this.couponService.createCoupon(request);

    save.subscribe({
      next: () => this.router.navigateByUrl('/admin'),
      error: (error) => {
        this.errorMessage.set(getErrorMessage(error)); // e.g. "A coupon with code 'SAVE20' already exists"
        this.submitting.set(false);
      },
    });
  }
}
