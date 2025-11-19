import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {Observable} from 'rxjs';
import {CountyResponse} from './county-response';
import {HttpClient} from '@angular/common/http';
import {InstitutionResponse} from './institution-response';

@Injectable({
  providedIn: 'root',
})
export class NewAppointmentService {

  private readonly countiesUrl = environment.apiUrl + '/appointment/counties';
  private readonly institutionsUrl = environment.apiUrl + '/appointment/institutions';
  private readonly servicesUrl = environment.apiUrl + '/appointment/services';

  constructor(private http: HttpClient) {
  }

  getAllCounties(): Observable<CountyResponse[]> {
    return this.http.get<CountyResponse[]>(this.countiesUrl);
  }

  getInstitutionsByCounty(countyId: number): Observable<InstitutionResponse[]> {
    return this.http.get<InstitutionResponse[]>(this.institutionsUrl + '/' + countyId);
  }

  getServicesByInstitutionType(institutionType: string): Observable<string[]> {
    return this.http.get<string[]>(this.servicesUrl + '/' + institutionType);
  }
}
