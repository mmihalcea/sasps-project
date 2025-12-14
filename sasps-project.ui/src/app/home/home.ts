import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Button } from 'primeng/button';
import { RouterLink } from '@angular/router';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { LoginComponent } from '../login/login';
import { environment } from '../../environments/environment';

interface Appointment {
  id: number;
  title: string;
  appointmentTime: string;
  status: string;
  serviceType: string;
}

interface DashboardStats {
  totalAppointments: number;
  pendingAppointments: number;
  confirmedAppointments: number;
  completedAppointments: number;
}

@Component({
  selector: 'app-home',
  imports: [CommonModule, Button, RouterLink, CardModule, TableModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
  standalone: true,
})
export class Home implements OnInit {
  isLoggedIn = false;
  isAdmin = false;
  userName = '';
  recentAppointments: Appointment[] = [];
  stats: DashboardStats = {
    totalAppointments: 0,
    pendingAppointments: 0,
    confirmedAppointments: 0,
    completedAppointments: 0,
  };
  loading = false;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.isLoggedIn = LoginComponent.isLoggedIn();
    this.isAdmin = LoginComponent.isAdmin();

    if (this.isLoggedIn) {
      const user = LoginComponent.getCurrentUser();
      this.userName = user.name;
      this.loadDashboardData();
    }
  }

  loadDashboardData(): void {
    this.loading = true;

    if (this.isAdmin) {
      // Admin Load all appointments for statistics
      this.http.get<Appointment[]>(`${environment.apiUrl}/appointment/all`).subscribe({
        next: (appointments) => {
          this.calculateStats(appointments);
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading appointments:', error);
          this.loading = false;
        },
      });
    } else {
      // Normal user Load their appointments
      const user = LoginComponent.getCurrentUser();
      this.http
        .get<Appointment[]>(`${environment.apiUrl}/appointment/customer/${user.email}`)
        .subscribe({
          next: (appointments) => {
            this.recentAppointments = appointments.slice(0, 5); // Latest 5
            this.calculateStats(appointments);
            this.loading = false;
          },
          error: (error) => {
            console.error('Error loading appointments:', error);
            this.loading = false;
          },
        });
    }
  }

  calculateStats(appointments: Appointment[]): void {
    this.stats.totalAppointments = appointments.length;
    this.stats.pendingAppointments = appointments.filter((a) => a.status === 'PENDING').length;
    this.stats.confirmedAppointments = appointments.filter((a) => a.status === 'CONFIRMED').length;
    this.stats.completedAppointments = appointments.filter((a) => a.status === 'COMPLETED').length;
  }

  getStatusClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'PENDING':
        return 'bg-yellow-500 text-white px-2 py-1 rounded';
      case 'CONFIRMED':
        return 'bg-green-500 text-white px-2 py-1 rounded';
      case 'COMPLETED':
        return 'bg-blue-500 text-white px-2 py-1 rounded';
      case 'CANCELLED':
        return 'bg-red-500 text-white px-2 py-1 rounded';
      default:
        return 'bg-gray-500 text-white px-2 py-1 rounded';
    }
  }
}
