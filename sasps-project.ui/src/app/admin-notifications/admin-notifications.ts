import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TableModule } from 'primeng/table';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { Select } from 'primeng/select';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginComponent } from '../login/login';

interface Notification {
  id: number;
  userId: number;
  message: string;
  type: string;
  status: string;
  method: string;
  recipientEmail: string;
  recipientPhone: string;
  sentAt: string;
  createdAt: string;
}

@Component({
  selector: 'app-admin-notifications',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, CardModule, TagModule, ButtonModule, Select],
  templateUrl: './admin-notifications.html',
  styleUrl: './admin-notifications.css',
})
export class AdminNotificationsComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);

  notifications: Notification[] = [];
  loading = false;

  statusOptions = [
    { label: 'All', value: null },
    { label: 'Pending', value: 'PENDING' },
    { label: 'Sent', value: 'SENT' },
    { label: 'Failed', value: 'FAILED' },
    { label: 'Skipped', value: 'SKIPPED' },
  ];

  selectedStatus: string | null = null;

  ngOnInit() {
    // Check if user is admin - baseline security
    if (!LoginComponent.isAdmin()) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadNotifications();
  }

  // Load all notifications with optional status filter
  loadNotifications() {
    this.loading = true;

    const user = LoginComponent.getCurrentUser();
    const headers = new HttpHeaders({
      'X-User-Role': user.role,
    });

    const url = this.selectedStatus
      ? `http://localhost:8080/api/notifications/status/${this.selectedStatus}`
      : 'http://localhost:8080/api/notifications/all';

    this.http.get<Notification[]>(url, { headers }).subscribe({
      next: (data) => {
        this.notifications = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Failed to load notifications', error);
        this.loading = false;
      },
    });
  }

  // Get severity for status tag
  getStatusSeverity(status: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' {
    switch (status) {
      case 'SENT':
        return 'success';
      case 'PENDING':
        return 'info';
      case 'FAILED':
        return 'danger';
      case 'SKIPPED':
        return 'warn';
      default:
        return 'secondary';
    }
  }

  // Get severity for type tag
  getTypeSeverity(type: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' {
    switch (type) {
      case 'CONFIRMATION':
        return 'success';
      case 'REMINDER':
        return 'warn';
      case 'CANCELLATION':
        return 'danger';
      case 'WELCOME':
        return 'info';
      default:
        return 'secondary';
    }
  }

  // Logout
  logout() {
    LoginComponent.logout();
    this.router.navigate(['/login']);
  }

  // Filter by status
  onStatusChange() {
    this.loadNotifications();
  }

  // Export appointments to CSV
  exportToCSV() {
    const user = LoginComponent.getCurrentUser();
    const headers = new HttpHeaders({
      'X-User-Role': user.role,
    });

    this.http
      .get('http://localhost:8080/api/appointment/export/csv', {
        headers,
        responseType: 'blob',
      })
      .subscribe({
        next: (data) => {
          const blob = new Blob([data], { type: 'text/csv' });
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `appointments_${new Date().getTime()}.csv`;
          link.click();
          window.URL.revokeObjectURL(url);
        },
        error: (error) => {
          console.error('Failed to export CSV', error);
          alert('Failed to export appointments to CSV');
        },
      });
  }

  // Export appointments to PDF
  exportToPDF() {
    const user = LoginComponent.getCurrentUser();
    const headers = new HttpHeaders({
      'X-User-Role': user.role,
    });

    this.http
      .get('http://localhost:8080/api/appointment/export/pdf', {
        headers,
        responseType: 'blob',
      })
      .subscribe({
        next: (data) => {
          const blob = new Blob([data], { type: 'text/html' });
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `appointments_${new Date().getTime()}.html`;
          link.click();
          window.URL.revokeObjectURL(url);
        },
        error: (error) => {
          console.error('Failed to export PDF', error);
          alert('Failed to export appointments to PDF');
        },
      });
  }
          alert('Failed to export appointments to PDF');
        },
      });
  }
}
