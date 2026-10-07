import { Component, inject, input, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth-service';
import { getErrorMessage } from '../../../core/utils/error-message';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  // Filled from the URL: /login?returnUrl=/saved  (thanks to withComponentInputBinding)
  readonly returnUrl = input<string>();

  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  // Each field: [starting value, validators]
  protected readonly form = this.formBuilder.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched(); // show the error messages under every field
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set(null);

    const { email, password } = this.form.getRawValue();
    this.authService.login({ email: email.trim(), password }).subscribe({
      next: () => this.router.navigateByUrl(this.returnUrl() ?? '/coupons'),
      error: (error) => {
        this.errorMessage.set(getErrorMessage(error));
        this.submitting.set(false);
      },
    });
  }
}
