import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { environment } from '../../environments/environment';
import { Button } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import { Table, TableModule } from 'primeng/table';

interface Appointment {
  id: number;
  title: string;
  appointmentTime: string;
  status: string;
  serviceType: string;
  institutionName?: string;
}

@Component({
  selector: 'app-user-appointments',
  imports: [CommonModule, FormsModule, Button, InputText, TableModule],
  templateUrl: './user-appointments.html',
  styleUrl: './user-appointments.css',
  standalone: true,
})
export class UserAppointments implements OnInit {
  email: string = '';
  appointments: Appointment[] = [];
  loading: boolean = false;
  searched: boolean = false;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {}

  searchAppointments() {
    if (!this.email || this.email.trim() === '') {
      return;
    }

    this.loading = true;
    this.searched = true;
    this.http
      .get<Appointment[]>(`${environment.apiUrl}/appointment/customer/${this.email}`)
      .subscribe({
        next: (data) => {
          this.appointments = data;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error fetching appointments:', error);
          this.appointments = [];
          this.loading = false;
        },
      });
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
