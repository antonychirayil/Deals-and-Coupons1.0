import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

// What the caller passes in when opening the dialog
export interface ConfirmDialogData {
  title: string;
  message: string;
  confirmText: string;
}

/**
 * A reusable "Are you sure?" dialog. Open it with:
 *   dialog.open(ConfirmDialog, { data: {...} }).afterClosed().subscribe(confirmed => ...)
 * It closes with true (confirm button) or undefined (Cancel / Esc / clicking outside).
 */
@Component({
  selector: 'app-confirm-dialog',
  imports: [MatDialogModule, MatButtonModule],
  templateUrl: './confirm-dialog.html',
  styleUrl: './confirm-dialog.css',
})
export class ConfirmDialog {
  protected readonly data = inject<ConfirmDialogData>(MAT_DIALOG_DATA);
}
