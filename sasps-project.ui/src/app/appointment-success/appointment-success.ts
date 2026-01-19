import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { DividerModule } from 'primeng/divider';
import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'app-appointment-success',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    CardModule,
    ButtonModule,
    TagModule,
    DividerModule,
    TooltipModule,
  ],
  templateUrl: './appointment-success.html',
  styleUrl: './appointment-success.css',
})
export class AppointmentSuccessComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  appointmentId = signal<string>('');
  appointmentData = signal<any>(null);
  qrCodeUrl = signal<string>('');
  showConfetti = signal(true);

  ngOnInit() {
    // Get appointment data from route state
    const state = history.state;
    console.log('Success page state:', state);
    
    if (state?.appointment) {
      const appointment = state.appointment;
      this.appointmentId.set(String(appointment.id));
      this.appointmentData.set(appointment);
      this.generateQRCode(String(appointment.id));
    } else {
      // Fallback to query param
      this.route.queryParams.subscribe(params => {
        if (params['id']) {
          this.appointmentId.set(params['id']);
          this.generateQRCode(params['id']);
        }
      });
    }

    // Trigger confetti
    this.launchConfetti();

    // Hide confetti after animation
    setTimeout(() => {
      this.showConfetti.set(false);
    }, 5000);
  }

  generateQRCode(appointmentId: string) {
    // Using a free QR code API
    const qrData = encodeURIComponent(`SASPS-APPOINTMENT:${appointmentId}`);
    this.qrCodeUrl.set(`https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${qrData}&bgcolor=ffffff&color=6366f1`);
  }

  launchConfetti() {
    // Create confetti elements
    const colors = ['#6366f1', '#8b5cf6', '#10b981', '#f59e0b', '#ec4899', '#06b6d4'];
    const confettiContainer = document.createElement('div');
    confettiContainer.className = 'confetti-container';
    confettiContainer.id = 'confetti';
    document.body.appendChild(confettiContainer);

    for (let i = 0; i < 150; i++) {
      const confetti = document.createElement('div');
      confetti.className = 'confetti-piece';
      confetti.style.left = Math.random() * 100 + 'vw';
      confetti.style.animationDelay = Math.random() * 3 + 's';
      confetti.style.backgroundColor = colors[Math.floor(Math.random() * colors.length)];
      confetti.style.transform = `rotate(${Math.random() * 360}deg)`;
      confettiContainer.appendChild(confetti);
    }

    // Remove confetti after animation
    setTimeout(() => {
      confettiContainer.remove();
    }, 6000);
  }

  downloadQR() {
    const link = document.createElement('a');
    link.href = this.qrCodeUrl();
    link.download = `programare-${this.appointmentId()}.png`;
    link.click();
  }

  shareAppointment() {
    const shareData = {
      title: 'Programare SASPS',
      text: `Am creat o programare cu ID: ${this.appointmentId()}`,
      url: window.location.href,
    };

    if (navigator.share) {
      navigator.share(shareData);
    } else {
      // Fallback - copy to clipboard
      navigator.clipboard.writeText(`Programare SASPS - ID: ${this.appointmentId()}\n${window.location.href}`);
      alert('Link copiat în clipboard!');
    }
  }

  printAppointment() {
    window.print();
  }

  addToCalendar() {
    const data = this.appointmentData();
    if (!data) return;

    const startDate = new Date(data.appointmentTime);
    const endDate = new Date(startDate.getTime() + 30 * 60000); // +30 min

    const formatDate = (d: Date) => d.toISOString().replace(/-|:|\.\d{3}/g, '');

    const calendarUrl = `https://calendar.google.com/calendar/render?action=TEMPLATE&text=${encodeURIComponent('Programare ' + (data.serviceName || 'SASPS'))}&dates=${formatDate(startDate)}/${formatDate(endDate)}&details=${encodeURIComponent('ID Programare: ' + this.appointmentId())}&location=${encodeURIComponent(data.institutionName || '')}`;

    window.open(calendarUrl, '_blank');
  }

  goHome() {
    this.router.navigate(['/home']);
  }

  newAppointment() {
    this.router.navigate(['/new-appointment']);
  }
}
