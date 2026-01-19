import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { TagModule } from 'primeng/tag';
import { ProgressBarModule } from 'primeng/progressbar';
import { TooltipModule } from 'primeng/tooltip';
import { DividerModule } from 'primeng/divider';
import { SkeletonModule } from 'primeng/skeleton';
import { MessageModule } from 'primeng/message';

interface InstitutionRecommendation {
  institutionId: number;
  institutionName: string;
  county: string;
  address: string;
  score: number;
  reason: string;
  rank: number;
  distance?: number;
  waitTimeHours?: number;
  rating?: number;
  totalReviews?: number;
  occupancyRate?: number;
  availableSlots?: number;
  boosted?: boolean;
  boostReason?: string;
}

interface RecommendationResponse {
  recommendations: InstitutionRecommendation[];
  totalResults: number;
  strategyUsed: string;
  strategyDescription: string;
  processingTimeMs: number;
}

interface StrategyInfo {
  name: string;
  description: string;
}

@Component({
  selector: 'app-recommendations',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardModule,
    ButtonModule,
    SelectModule,
    TagModule,
    ProgressBarModule,
    TooltipModule,
    DividerModule,
    SkeletonModule,
    MessageModule,
  ],
  templateUrl: './recommendations.html',
  styleUrl: './recommendations.css',
})
export class RecommendationsComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);

  // State
  loading = signal(false);
  recommendations = signal<InstitutionRecommendation[]>([]);
  response = signal<RecommendationResponse | null>(null);
  strategies = signal<StrategyInfo[]>([]);
  error = signal<string | null>(null);

  // Form
  selectedService = signal('DECLARATIE_FISCALA');
  selectedCounty = signal('BUCURESTI');
  selectedStrategy = signal('NEAREST_LOCATION');

  services = [
    { label: 'Declarație Fiscală', value: 'DECLARATIE_FISCALA' },
    { label: 'Carte de Identitate', value: 'CARTE_IDENTITATE' },
    { label: 'Certificat de Urbanism', value: 'CERTIFICAT_URBANISM' },
    { label: 'Autorizație de Construcție', value: 'AUTORIZATIE_CONSTRUCTIE' },
    { label: 'Taxe și Impozite', value: 'taxe' },
  ];

  counties = [
    { label: 'București', value: 'BUCURESTI' },
    { label: 'Cluj', value: 'CLUJ' },
    { label: 'Timiș', value: 'TIMIS' },
    { label: 'Iași', value: 'IASI' },
    { label: 'Constanța', value: 'CONSTANTA' },
    { label: 'Brașov', value: 'BRASOV' },
    { label: 'Sibiu', value: 'SIBIU' },
    { label: 'Dolj', value: 'DOLJ' },
  ];

  strategyOptions = computed(() =>
    this.strategies().map(s => ({
      label: s.description,
      value: s.name,
    }))
  );

  strategyIcons: Record<string, string> = {
    NEAREST_LOCATION: 'pi-map-marker',
    FASTEST_AVAILABILITY: 'pi-bolt',
    BEST_RATED: 'pi-star',
    LEAST_BUSY: 'pi-chart-bar',
  };

  ngOnInit() {
    this.loadStrategies();
    this.getRecommendations();
  }

  loadStrategies() {
    this.http.get<StrategyInfo[]>('/api/recommendations/strategies').subscribe({
      next: (data) => this.strategies.set(data),
      error: (err) => console.error('Error loading strategies:', err),
    });
  }

  getRecommendations() {
    this.loading.set(true);
    this.error.set(null);

    const params = new URLSearchParams({
      serviceType: this.selectedService(),
      userCounty: this.selectedCounty(),
      strategy: this.selectedStrategy(),
      maxResults: '5',
    });

    this.http
      .get<RecommendationResponse>(`/api/recommendations?${params}`)
      .subscribe({
        next: (data) => {
          this.response.set(data);
          this.recommendations.set(data.recommendations);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Error getting recommendations:', err);
          this.error.set('Nu s-au putut încărca recomandările');
          this.loading.set(false);
        },
      });
  }

  selectInstitution(recommendation: InstitutionRecommendation) {
    // Navighează la formularul de programare cu instituția preselectată
    this.router.navigate(['/new-appointment'], {
      queryParams: {
        institutionId: recommendation.institutionId,
        county: recommendation.county,
      },
    });
  }

  getScoreColor(score: number): string {
    if (score >= 80) return 'success';
    if (score >= 60) return 'info';
    if (score >= 40) return 'warn';
    return 'danger';
  }

  getScoreSeverity(score: number): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' {
    if (score >= 80) return 'success';
    if (score >= 60) return 'info';
    if (score >= 40) return 'warn';
    return 'danger';
  }

  getStrategyIcon(): string {
    return this.strategyIcons[this.selectedStrategy()] || 'pi-cog';
  }

  formatDistance(distance: number): string {
    if (distance < 1) return `${Math.round(distance * 1000)} m`;
    return `${distance.toFixed(1)} km`;
  }

  formatWaitTime(hours: number): string {
    if (hours < 24) return `${hours}h`;
    const days = Math.floor(hours / 24);
    return `${days} zile`;
  }
}
