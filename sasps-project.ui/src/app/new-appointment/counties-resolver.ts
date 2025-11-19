import { ResolveFn } from '@angular/router';
import {inject} from '@angular/core';
import {NewAppointmentService} from './new-appointment-service';
import {CountyResponse} from './county-response';

export const countiesResolver: ResolveFn<CountyResponse[]> = (route, state) => {
  let newAppointmentService = inject(NewAppointmentService);
  return newAppointmentService.getAllCounties();
};
