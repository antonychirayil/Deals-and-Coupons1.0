import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  // Data for the "How it works" section. @for in the template repeats the HTML for each item.
  protected readonly steps = [
    { icon: '🔍', title: 'Find', text: 'Browse coupons from your favourite stores, or search by brand.' },
    { icon: '👀', title: 'Reveal', text: 'Click "Show code" to see the coupon code.' },
    { icon: '💸', title: 'Save', text: 'Use the code at checkout and pay less.' },
  ];
}
