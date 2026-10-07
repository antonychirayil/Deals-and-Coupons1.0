import { HttpErrorResponse } from '@angular/common/http';

/**
 * Turns an HTTP error into a sentence we can show the user.
 * Our services answer errors with ProblemDetail JSON: { detail: "...", errors: { field: "message" } }
 */
export function getErrorMessage(error: HttpErrorResponse): string {
  if (error.status === 0) {
    return 'Cannot reach the server. Is the backend running?';
  }
  if (error.status === 403) {
    return 'You are not allowed to do this.';
  }

  const body = error.error;
  if (body?.errors) {
    return Object.values(body.errors).join('. '); // validation errors, one per field
  }
  return body?.detail ?? 'Something went wrong. Please try again.';
}
