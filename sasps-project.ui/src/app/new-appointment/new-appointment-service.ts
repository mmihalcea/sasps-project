import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {map, Observable} from 'rxjs';
import {CountyResponse} from './county-response';
import {HttpClient} from '@angular/common/http';
import {InstitutionResponse} from './institution-response';
import {InstitutionDetailsResponse} from './institution-details-response';

@Injectable({
  providedIn: 'root',
})
export class NewAppointmentService {

  private readonly countiesUrl = environment.apiUrl + '/appointment/counties';
  private readonly institutionsUrl = environment.apiUrl + '/appointment/institutions';
  private readonly institutionUrl = environment.apiUrl + '/institution';

  constructor(private http: HttpClient) {
  }

  getAllCounties(): Observable<CountyResponse[]> {
    return this.http.get<CountyResponse[]>(this.countiesUrl);
  }

  getInstitutionsByCounty(countyId: number): Observable<InstitutionResponse[]> {
    return this.http.get<InstitutionResponse[]>(this.institutionsUrl + '/' + countyId);
  }

  getInstitutionDetails(institutionType: string): Observable<InstitutionDetailsResponse> {
    return this.http.get<InstitutionDetailsResponse>(this.institutionUrl + '/' + institutionType).pipe(
      map((data)=>{
       return {...data, availability: data.availability.map(av=> new Date((String(av))))};}
    ));
  }
}
