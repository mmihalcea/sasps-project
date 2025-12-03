import {PublicServiceDetailsResponse} from './public-service-details-response';

export interface InstitutionDetailsResponse {
  id: number;
  openingTime: Date;
  closingTime: Date;
  maxAppointmentsPerDay: number;
  availableServices: PublicServiceDetailsResponse[];
  availability: Date[];
}
