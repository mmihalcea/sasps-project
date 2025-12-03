import {Component, computed, inject, OnInit} from '@angular/core';
import {ActivatedRoute, RouterModule, RouterOutlet} from '@angular/router';
import {toSignal} from '@angular/core/rxjs-interop';
import {CountyResponse} from './county-response';
import { StepperModule } from 'primeng/stepper';
import {CommonModule} from '@angular/common';
import {Button} from 'primeng/button';
import {Select} from 'primeng/select';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {NewAppointmentService} from './new-appointment-service';
import {InstitutionResponse} from './institution-response';
import { DatePickerModule } from 'primeng/datepicker';
import {SelectButton} from 'primeng/selectbutton';
import {InstitutionDetailsResponse} from './institution-details-response';

@Component({
  selector: 'app-new-appointment',
  imports: [StepperModule, CommonModule, RouterModule, Button, Select, ReactiveFormsModule, FormsModule, DatePickerModule, SelectButton],
  templateUrl: './new-appointment.html',
  standalone: true,
  styleUrl: './new-appointment.css',
})
export class NewAppointment implements OnInit {
 counties: CountyResponse[] | undefined = undefined;
 institutions: InstitutionResponse[] | undefined = undefined;
 institutionDetails: InstitutionDetailsResponse | undefined = undefined;
 appointmentForm: FormGroup;
  protected today: Date = new Date();
  protected timeOptions: Date[] = [];

  constructor( private route: ActivatedRoute, private fb: FormBuilder, private newAppointmentService: NewAppointmentService) {
    this.appointmentForm = this.fb.group({
        institution: this.fb.group({
          county: [undefined, [Validators.required]],
          institution: [undefined, [Validators.required]],
        }),
        appointment: this.fb.group({
          service: [undefined, [Validators.required]],
          date: [undefined, [Validators.required]],
          time: [undefined, [Validators.required]],
        })
    });

    this.appointmentForm.get('institution.county')?.valueChanges.subscribe(value =>{
      this.appointmentForm.get('institution.institution')?.reset();
      this.newAppointmentService.getInstitutionsByCounty(value).subscribe(res => this.institutions = res);
    });

  }

  ngOnInit(): void {
    this.route.data.subscribe(({counties}) => {
      this.counties = counties;
    });
    }

  protected onSubmit() {

  }

  protected getServicesByInstitutionType() {
    let institutionId = this.appointmentForm.get('institution.institution')?.value;
    let institution = this.institutions?.find(institution=> institution.id === institutionId);
    this.newAppointmentService.getInstitutionDetails(String(institution?.institutionType)).subscribe(res => {
      this.institutionDetails = res;
      this.timeOptions = this.institutionDetails.availability.filter(availability=> availability.getDay() === new Date().getDay());
    })

  }

}
