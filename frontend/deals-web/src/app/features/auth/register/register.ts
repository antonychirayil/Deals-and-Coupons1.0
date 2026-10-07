import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth-service';
import { getErrorMessage } from '../../../core/utils/error-message';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  // Same rules as RegisterRequest in auth-service, so most mistakes are caught before sending
  protected readonly form = this.formBuilder.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]],
  });

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set(null);

    const { name, email, password } = this.form.getRawValue();
    // register() also logs the new user in, so we can go straight to the coupons
    this.authService.register({ name: name.trim(), email: email.trim(), password }).subscribe({
      next: () => this.router.navigateByUrl('/coupons'),
      error: (error) => {
        this.errorMessage.set(getErrorMessage(error));
        this.submitting.set(false);
      },
    });
  }
}
