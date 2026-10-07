import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { Footer } from './shared/footer/footer';
import { Navbar } from './shared/navbar/navbar';

// The root component: the page frame that stays the same on every page
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, Footer], // components used in app.html must be listed here
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {}
