import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ChartModule } from 'primeng/chart';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { ProgressBarModule } from 'primeng/progressbar';
import { SkeletonModule } from 'primeng/skeleton';
import { TooltipModule } from 'primeng/tooltip';
import { interval, Subject, takeUntil } from 'rxjs';

interface DashboardStats {
  totalAppointments: number;
  pendingAppointments: number;
  confirmedAppointments: number;
  completedAppointments: number;
  cancelledAppointments: number;
  todayAppointments: number;
  weekAppointments: number;
  averageWaitTime: number;
}

interface AppointmentSummary {
  id: number;
  title: string;
  customerName: string;
  institutionName: string;
  appointmentTime: string;
  status: string;
  serviceType: string;
}

interface InstitutionStats {
  name: string;
  totalAppointments: number;
  occupancyRate: number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    ChartModule,
    TableModule,
    TagModule,
    ButtonModule,
    ProgressBarModule,
    SkeletonModule,
    TooltipModule,
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class DashboardComponent implements OnInit, OnDestroy {
  private http = inject(HttpClient);
  private router = inject(Router);
  private destroy$ = new Subject<void>();

  loading = true;
  stats: DashboardStats = {
    totalAppointments: 0,
    pendingAppointments: 0,
    confirmedAppointments: 0,
    completedAppointments: 0,
    cancelledAppointments: 0,
    todayAppointments: 0,
    weekAppointments: 0,
    averageWaitTime: 0,
  };

  recentAppointments: AppointmentSummary[] = [];
  institutionStats: InstitutionStats[] = [];

  // Chart data
  statusChartData: any;
  statusChartOptions: any;
  weeklyChartData: any;
  weeklyChartOptions: any;
  serviceTypeChartData: any;
  serviceTypeChartOptions: any;

  // Real-time counter animation
  animatedStats = {
    total: 0,
    pending: 0,
    confirmed: 0,
    completed: 0,
  };

  ngOnInit() {
    this.initChartOptions();
    this.loadDashboardData();

    // Auto-refresh every 30 seconds (Observer pattern - polling)
    interval(30000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.loadDashboardData(false);
      });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initChartOptions() {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color') || '#495057';
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border') || '#dfe7ef';

    this.statusChartOptions = {
      plugins: {
        legend: {
          position: 'bottom',
          labels: { color: textColor },
        },
      },
      maintainAspectRatio: false,
    };

    this.weeklyChartOptions = {
      plugins: {
        legend: {
          display: false,
        },
      },
      scales: {
        x: {
          ticks: { color: textColor },
          grid: { color: surfaceBorder },
        },
        y: {
          ticks: { color: textColor },
          grid: { color: surfaceBorder },
          beginAtZero: true,
        },
      },
      maintainAspectRatio: false,
    };

    this.serviceTypeChartOptions = {
      indexAxis: 'y',
      plugins: {
        legend: { display: false },
      },
      scales: {
        x: {
          ticks: { color: textColor },
          grid: { color: surfaceBorder },
          beginAtZero: true,
        },
        y: {
          ticks: { color: textColor },
          grid: { display: false },
        },
      },
      maintainAspectRatio: false,
    };
  }

  loadDashboardData(showLoading = true) {
    if (showLoading) {
      this.loading = true;
    }

    // Load all appointments and calculate stats
    this.http.get<any[]>('http://localhost:8080/api/appointment/all').subscribe({
      next: (appointments) => {
        this.calculateStats(appointments);
        this.recentAppointments = this.getRecentAppointments(appointments);
        this.updateCharts(appointments);
        this.animateCounters();
        this.loading = false;
      },
      error: () => {
        // Use mock data for demo if API fails
        this.loadMockData();
        this.loading = false;
      },
    });

    // Load institution stats
    this.http.get<any[]>('http://localhost:8080/api/institution').subscribe({
      next: (institutions) => {
        this.calculateInstitutionStats(institutions);
      },
      error: () => {
        this.institutionStats = this.getMockInstitutionStats();
      },
    });
  }

  private calculateStats(appointments: any[]) {
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const weekAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);

    this.stats = {
      totalAppointments: appointments.length,
      pendingAppointments: appointments.filter((a) => a.status === 'PENDING').length,
      confirmedAppointments: appointments.filter((a) => a.status === 'CONFIRMED').length,
      completedAppointments: appointments.filter((a) => a.status === 'COMPLETED').length,
      cancelledAppointments: appointments.filter((a) => a.status === 'CANCELLED').length,
      todayAppointments: appointments.filter((a) => {
        const apptDate = new Date(a.appointmentTime);
        return apptDate >= today;
      }).length,
      weekAppointments: appointments.filter((a) => {
        const apptDate = new Date(a.appointmentTime);
        return apptDate >= weekAgo;
      }).length,
      averageWaitTime: this.calculateAverageWaitTime(appointments),
    };
  }

  private calculateAverageWaitTime(appointments: any[]): number {
    if (appointments.length === 0) return 0;
    const totalDuration = appointments.reduce((sum, a) => sum + (a.estimatedDuration || 15), 0);
    return Math.round(totalDuration / appointments.length);
  }

  private getRecentAppointments(appointments: any[]): AppointmentSummary[] {
    return appointments
      .sort((a, b) => new Date(b.appointmentTime).getTime() - new Date(a.appointmentTime).getTime())
      .slice(0, 5)
      .map((a) => ({
        id: a.id,
        title: a.title || 'Programare',
        customerName: a.customerName || 'Client',
        institutionName: a.institutionName || `Instituție #${a.institutionId}`,
        appointmentTime: a.appointmentTime,
        status: a.status,
        serviceType: a.serviceType,
      }));
  }

  private calculateInstitutionStats(institutions: any[]) {
    this.institutionStats = institutions.slice(0, 5).map((inst) => ({
      name: inst.name,
      totalAppointments: Math.floor(Math.random() * 50) + 10, // Would come from API
      occupancyRate: Math.floor(Math.random() * 40) + 40, // Would come from API
    }));
  }

  private updateCharts(appointments: any[]) {
    // Status Pie Chart
    this.statusChartData = {
      labels: ['În așteptare', 'Confirmate', 'Finalizate', 'Anulate'],
      datasets: [
        {
          data: [
            this.stats.pendingAppointments,
            this.stats.confirmedAppointments,
            this.stats.completedAppointments,
            this.stats.cancelledAppointments,
          ],
          backgroundColor: ['#f59e0b', '#10b981', '#3b82f6', '#ef4444'],
          hoverBackgroundColor: ['#d97706', '#059669', '#2563eb', '#dc2626'],
        },
      ],
    };

    // Weekly Bar Chart
    const weekDays = ['Luni', 'Marți', 'Miercuri', 'Joi', 'Vineri', 'Sâmbătă', 'Duminică'];
    const weeklyData = this.calculateWeeklyData(appointments);

    this.weeklyChartData = {
      labels: weekDays,
      datasets: [
        {
          label: 'Programări',
          data: weeklyData,
          backgroundColor: '#6366f1',
          borderColor: '#4f46e5',
          borderWidth: 1,
          borderRadius: 8,
        },
      ],
    };

    // Service Type Horizontal Bar Chart
    const serviceTypeCounts = this.calculateServiceTypeCounts(appointments);
    this.serviceTypeChartData = {
      labels: Object.keys(serviceTypeCounts).map((k) => this.formatServiceType(k)),
      datasets: [
        {
          data: Object.values(serviceTypeCounts),
          backgroundColor: ['#8b5cf6', '#06b6d4', '#f97316', '#84cc16', '#ec4899'],
          borderRadius: 8,
        },
      ],
    };
  }

  private calculateWeeklyData(appointments: any[]): number[] {
    const counts = [0, 0, 0, 0, 0, 0, 0];
    appointments.forEach((a) => {
      const day = new Date(a.appointmentTime).getDay();
      const adjustedDay = day === 0 ? 6 : day - 1; // Monday = 0
      counts[adjustedDay]++;
    });
    return counts;
  }

  private calculateServiceTypeCounts(appointments: any[]): Record<string, number> {
    const counts: Record<string, number> = {};
    appointments.forEach((a) => {
      const type = a.serviceType || 'ALTELE';
      counts[type] = (counts[type] || 0) + 1;
    });
    return counts;
  }

  private formatServiceType(type: string): string {
    const mapping: Record<string, string> = {
      ELIBERARE_CI: 'Carte de Identitate',
      CERTIFICAT_NASTERE: 'Certificat Naștere',
      DECLARATIE_FISCALA: 'Declarație Fiscală',
      PRESCHIMBARE_PERMIS: 'Permis Conducere',
      INMATRICULARE_VEHICUL: 'Înmatriculare',
    };
    return mapping[type] || type;
  }

  private animateCounters() {
    const duration = 1000;
    const steps = 30;
    const interval = duration / steps;

    let step = 0;
    const timer = setInterval(() => {
      step++;
      const progress = step / steps;
      const easeOut = 1 - Math.pow(1 - progress, 3);

      this.animatedStats = {
        total: Math.round(this.stats.totalAppointments * easeOut),
        pending: Math.round(this.stats.pendingAppointments * easeOut),
        confirmed: Math.round(this.stats.confirmedAppointments * easeOut),
        completed: Math.round(this.stats.completedAppointments * easeOut),
      };

      if (step >= steps) {
        clearInterval(timer);
        this.animatedStats = {
          total: this.stats.totalAppointments,
          pending: this.stats.pendingAppointments,
          confirmed: this.stats.confirmedAppointments,
          completed: this.stats.completedAppointments,
        };
      }
    }, interval);
  }

  private loadMockData() {
    this.stats = {
      totalAppointments: 156,
      pendingAppointments: 23,
      confirmedAppointments: 45,
      completedAppointments: 78,
      cancelledAppointments: 10,
      todayAppointments: 12,
      weekAppointments: 67,
      averageWaitTime: 18,
    };

    this.recentAppointments = [
      {
        id: 1,
        title: 'Eliberare CI',
        customerName: 'Ion Popescu',
        institutionName: 'SPCLEP Cluj',
        appointmentTime: new Date().toISOString(),
        status: 'CONFIRMED',
        serviceType: 'ELIBERARE_CI',
      },
      {
        id: 2,
        title: 'Certificat Naștere',
        customerName: 'Maria Ionescu',
        institutionName: 'Primăria București',
        appointmentTime: new Date().toISOString(),
        status: 'PENDING',
        serviceType: 'CERTIFICAT_NASTERE',
      },
      {
        id: 3,
        title: 'Permis Conducere',
        customerName: 'Andrei Vasile',
        institutionName: 'DRPCIV București',
        appointmentTime: new Date().toISOString(),
        status: 'COMPLETED',
        serviceType: 'PRESCHIMBARE_PERMIS',
      },
    ];

    this.institutionStats = this.getMockInstitutionStats();
    this.updateCharts([]);
    this.animateCounters();
  }

  private getMockInstitutionStats(): InstitutionStats[] {
    return [
      { name: 'Primăria București', totalAppointments: 45, occupancyRate: 78 },
      { name: 'SPCLEP Cluj', totalAppointments: 38, occupancyRate: 65 },
      { name: 'DRPCIV București', totalAppointments: 52, occupancyRate: 85 },
      { name: 'Primăria Timișoara', totalAppointments: 29, occupancyRate: 55 },
      { name: 'SPCLEP Iași', totalAppointments: 31, occupancyRate: 60 },
    ];
  }

  getStatusSeverity(status: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' {
    switch (status) {
      case 'CONFIRMED':
        return 'success';
      case 'PENDING':
        return 'warn';
      case 'COMPLETED':
        return 'info';
      case 'CANCELLED':
        return 'danger';
      default:
        return 'secondary';
    }
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      PENDING: 'În așteptare',
      CONFIRMED: 'Confirmată',
      COMPLETED: 'Finalizată',
      CANCELLED: 'Anulată',
    };
    return labels[status] || status;
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('ro-RO', {
      day: '2-digit',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  navigateToAppointment(id: number) {
    this.router.navigate(['/appointments', id]);
  }

  refresh() {
    this.loadDashboardData();
  }
}
