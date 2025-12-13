import { Component, signal } from '@angular/core';

import { InputTextModule } from 'primeng/inputtext';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputGroupModule } from 'primeng/inputgroup';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { LoginComponent } from './login/login';

@Component({
  selector: 'app-root',
  imports: [
    CommonModule,
    InputTextModule,
    FormsModule,
    InputGroupModule,
    ButtonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  constructor(private router: Router) {}

  // Check if user is logged in - baseline approach with direct localStorage access
  isLoggedIn(): boolean {
    return LoginComponent.isLoggedIn();
  }

  // Check if current user is admin
  isAdmin(): boolean {
    return LoginComponent.isAdmin();
  }

  // Get current user name
  getCurrentUserName(): string {
    const user = LoginComponent.getCurrentUser();
    return user ? user.name : '';
  }

  // Logout
  logout() {
    LoginComponent.logout();
    this.router.navigate(['/login']);
  }
}
