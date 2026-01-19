import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { map, Observable } from 'rxjs';
import { CountyResponse } from './county-response';
import { HttpClient } from '@angular/common/http';
import { InstitutionResponse } from './institution-response';
import { InstitutionDetailsResponse } from './institution-details-response';
import {AvailabilityResponse} from './availability-response.model';
import {formatDate} from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class NewAppointmentService {
  private readonly countiesUrl = environment.apiUrl + '/appointment/counties';
  private readonly institutionsUrl = environment.apiUrl + '/appointment/institutions';
  private readonly availabilityUrl = environment.apiUrl + '/appointment/availability';
  private readonly institutionUrl = environment.apiUrl + '/institution';
  private readonly appointmentUrl = environment.apiUrl + '/appointment';

  constructor(private http: HttpClient) {}

  getAllCounties(): Observable<CountyResponse[]> {
    return this.http.get<CountyResponse[]>(this.countiesUrl);
  }

  getInstitutionsByCounty(countyId: number): Observable<InstitutionResponse[]> {
    return this.http.get<InstitutionResponse[]>(this.institutionsUrl + '/' + countyId);
  }

  getAvailability(institutionId: number, startDate: Date): Observable<AvailabilityResponse> {
    return this.http.get<AvailabilityResponse>(this.availabilityUrl + '?institutionId=' + institutionId + '&startDate=' + formatDate(startDate, 'ddMMyyyy', 'en-US'));
  }

  getInstitutionDetails(institutionType: string): Observable<InstitutionDetailsResponse> {
    return this.http
      .get<InstitutionDetailsResponse>(this.institutionUrl + '/' + institutionType)
      .pipe(
        map((data) => {
          return { ...data, availability: data.availability.map((av) => new Date(String(av))) };
        })
      );
  }

  saveAppointment(appointmentRequest: any): Observable<{id: number, message: string}> {
    return this.http.post<{id: number, message: string}>(this.appointmentUrl, appointmentRequest);
  }
}
