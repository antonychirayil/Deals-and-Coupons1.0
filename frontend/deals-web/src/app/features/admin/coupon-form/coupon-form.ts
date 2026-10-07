import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
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
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    MatIconModule,
    MatButtonModule,
    MatProgressBarModule,
  ],
  templateUrl: './coupon-form.html',
  styleUrl: './coupon-form.css',
})
export class CouponForm implements OnInit {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly couponService = inject(CouponService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  readonly id = input<string>(); // the :id part of the URL

  protected readonly isEdit = computed(() => this.id() !== undefined);
  protected readonly loadingCoupon = signal(false);
  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly categories = signal<string[]>([]);

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

  // toSignal() turns an Observable (the field's valueChanges) into a signal we can use in computed()
  private readonly categoryText = toSignal(this.form.controls.category.valueChanges, { initialValue: '' });

  // Autocomplete suggestions: existing categories that contain what was typed
  protected readonly categorySuggestions = computed(() => {
    const typed = this.categoryText().trim().toLowerCase();
    return this.categories().filter((name) => name.toLowerCase().includes(typed));
  });

  ngOnInit(): void {
    this.couponService.getCategories().subscribe((names) => this.categories.set(names));

    const id = this.id();
    if (id) {
      this.loadingCoupon.set(true);
      this.couponService.getCoupon(id).subscribe({
        // setValue() fills every field of the form at once
        next: (coupon) => {
          this.form.setValue({
            code: coupon.code,
            provider: coupon.provider,
            category: coupon.category,
            description: coupon.description ?? '',
            discount: coupon.discount,
            expiryDate: coupon.expiryDate,
          });
          this.loadingCoupon.set(false);
        },
        error: (error) => {
          this.errorMessage.set(getErrorMessage(error));
          this.loadingCoupon.set(false);
        },
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
      next: (coupon) => {
        this.snackBar.open(id ? `${coupon.code} updated` : `${coupon.code} created`, undefined, { duration: 3000 });
        this.router.navigateByUrl('/admin');
      },
      error: (error) => {
        this.errorMessage.set(getErrorMessage(error)); // e.g. "A coupon with code 'SAVE20' already exists"
        this.submitting.set(false);
      },
    });
  }
}
