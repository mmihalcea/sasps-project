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
import { SliderModule } from 'primeng/slider';
import { ToggleButtonModule } from 'primeng/togglebutton';

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
    SliderModule,
    ToggleButtonModule,
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
  selectedService = signal('Taxe si impozite locale');
  selectedCounty = signal('BUCURESTI');
  selectedStrategy = signal('NEAREST_LOCATION');

  // COMPOSITE Pattern - Mod avansat cu ponderi
  compositeMode = signal(false);
  strategyWeights = signal<Record<string, number>>({
    NEAREST_LOCATION: 25,
    FASTEST_AVAILABILITY: 25,
    BEST_RATED: 25,
    LEAST_BUSY: 25,
  });

  // Computed: suma ponderilor
  totalWeight = computed(() => {
    const weights = this.strategyWeights();
    return Object.values(weights).reduce((sum, w) => sum + w, 0);
  });

  // Computed: ponderile sunt valide (suma = 100)
  weightsValid = computed(() => this.totalWeight() === 100);

  // Servicii disponibile în baza de date
  services = [
    { label: 'Taxe și Impozite Locale', value: 'Taxe si impozite locale' },
    { label: 'Eliberare Carte de Identitate', value: 'Eliberare carte de identitate' },
    { label: 'Eliberare Certificate', value: 'Eliberare certificate' },
    { label: 'Preschimbare Permis de Conducere', value: 'Preschimbare permis de conducere' },
    { label: 'Înmatriculare Vehicul', value: 'Inmatriculare vehicul' },
  ];

  // Toate județele disponibile
  counties = [
    { label: 'București', value: 'BUCURESTI' },
    { label: 'Alba', value: 'ALBA' },
    { label: 'Arad', value: 'ARAD' },
    { label: 'Argeș', value: 'ARGES' },
    { label: 'Bacău', value: 'BACAU' },
    { label: 'Brașov', value: 'BRASOV' },
    { label: 'Cluj', value: 'CLUJ' },
    { label: 'Constanța', value: 'CONSTANTA' },
    { label: 'Dolj', value: 'DOLJ' },
    { label: 'Iași', value: 'IASI' },
    { label: 'Sibiu', value: 'SIBIU' },
    { label: 'Timiș', value: 'TIMIS' },
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

    // COMPOSITE Pattern - Construiește request diferit pentru mod avansat
    if (this.compositeMode()) {
      const requestBody = {
        serviceType: this.selectedService(),
        userCounty: this.selectedCounty(),
        strategy: 'COMPOSITE',
        maxResults: 5,
        strategyWeights: this.strategyWeights(),
      };

      this.http
        .post<RecommendationResponse>('/api/recommendations', requestBody)
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
    } else {
      // Mod simplu - request GET
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
  }

  // Actualizează o pondere specifică
  updateWeight(strategy: string, value: number) {
    this.strategyWeights.update(weights => ({
      ...weights,
      [strategy]: value,
    }));
  }

  // Toggle mod compozit
  toggleCompositeMode() {
    this.compositeMode.update(v => !v);
    if (!this.compositeMode()) {
      // Resetează la modul simplu
      this.getRecommendations();
    }
  }

  // Auto-normalizare ponderi la 100%
  normalizeWeights() {
    const weights = this.strategyWeights();
    const total = Object.values(weights).reduce((sum, w) => sum + w, 0);
    if (total > 0) {
      const normalized: Record<string, number> = {};
      for (const [key, value] of Object.entries(weights)) {
        normalized[key] = Math.round((value / total) * 100);
      }
      // Ajustează diferența de rotunjire
      const newTotal = Object.values(normalized).reduce((sum, w) => sum + w, 0);
      const diff = 100 - newTotal;
      const firstKey = Object.keys(normalized)[0];
      normalized[firstKey] += diff;
      this.strategyWeights.set(normalized);
    }
  }

  // Labels pentru strategii în modul compozit
  strategyLabels: Record<string, string> = {
    NEAREST_LOCATION: 'Distanță',
    FASTEST_AVAILABILITY: 'Disponibilitate',
    BEST_RATED: 'Rating',
    LEAST_BUSY: 'Aglomerație',
  };

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
