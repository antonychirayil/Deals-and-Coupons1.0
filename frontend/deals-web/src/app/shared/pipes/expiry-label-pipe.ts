import { Pipe, PipeTransform } from '@angular/core';

import { daysUntil } from '../utils/display';

/**
 * A custom pipe, used in templates like the built-in "date" pipe:
 *   {{ coupon.expiryDate | expiryLabel }}   ->   "Ends in 3 days"
 * Pipes are "pure" by default: Angular only re-runs them when the input value changes.
 */
@Pipe({
  name: 'expiryLabel',
})
export class ExpiryLabelPipe implements PipeTransform {
  transform(expiryDate: string): string {
    const days = daysUntil(expiryDate);

    if (days < 0) return 'Expired';
    if (days === 0) return 'Ends today';
    if (days === 1) return 'Ends tomorrow';
    if (days <= 7) return `Ends in ${days} days`;

    const [year, month, day] = expiryDate.split('-').map(Number);
    const formatted = new Date(year, month - 1, day).toLocaleDateString('en-IN', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
    });
    return `Valid till ${formatted}`;
  }
}
