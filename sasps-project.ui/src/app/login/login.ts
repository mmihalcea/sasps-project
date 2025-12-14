import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { MessageModule } from 'primeng/message';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    InputTextModule,
    PasswordModule,
    ButtonModule,
    CardModule,
    MessageModule,
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginComponent {
  private http = inject(HttpClient);
  private router = inject(Router);

  email = '';
  password = '';
  errorMessage = '';
  loading = false;

  // Super simple login, no proper state management
  login() {
    this.loading = true;
    this.errorMessage = '';

    this.http
      .post<any>('http://localhost:8080/api/auth/login', {
        email: this.email,
        password: this.password,
      })
      .subscribe({
        next: (response) => {
          // Store user in localStorage, no proper session management
          localStorage.setItem('currentUser', JSON.stringify(response));

          // Redirect based on role
          if (response.role === 'ADMIN') {
            this.router.navigate(['/admin-notifications']);
          } else {
            this.router.navigate(['/']);
          }

          this.loading = false;
        },
        error: (error) => {
          this.errorMessage = error.error?.error || 'Login failed';
          this.loading = false;
        },
      });
  }

  // Check if user is logged in
  static isLoggedIn(): boolean {
    return localStorage.getItem('currentUser') !== null;
  }

  // Get current user
  static getCurrentUser(): any {
    const user = localStorage.getItem('currentUser');
    return user ? JSON.parse(user) : null;
  }

  // Logout
  static logout() {
    localStorage.removeItem('currentUser');
  }

  // Check if current user is admin
  static isAdmin(): boolean {
    const user = LoginComponent.getCurrentUser();
    return user && user.role === 'ADMIN';
  }
}
