import { Component, computed, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule, RouterOutlet } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { CountyResponse } from './county-response';
import { StepperModule } from 'primeng/stepper';
import { CommonModule } from '@angular/common';
import { Button } from 'primeng/button';
import { Select } from 'primeng/select';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { NewAppointmentService } from './new-appointment-service';
import { InstitutionResponse } from './institution-response';
import { DatePickerModule } from 'primeng/datepicker';
import { SelectButton } from 'primeng/selectbutton';
import { InstitutionDetailsResponse } from './institution-details-response';

@Component({
  selector: 'app-new-appointment',
  imports: [
    StepperModule,
    CommonModule,
    RouterModule,
    Button,
    Select,
    ReactiveFormsModule,
    FormsModule,
    DatePickerModule,
    SelectButton,
  ],
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
  currentStep: number = 1;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private newAppointmentService: NewAppointmentService
  ) {
    this.appointmentForm = this.fb.group({
      institution: this.fb.group({
        county: [undefined, [Validators.required]],
        institution: [undefined, [Validators.required]],
      }),
      appointment: this.fb.group({
        service: [undefined, [Validators.required]],
        date: [undefined, [Validators.required]],
        time: [undefined, [Validators.required]],
      }),
      user: this.fb.group({
        name: ['Test User', [Validators.required]],
        email: ['test@test.com', [Validators.required, Validators.email]],
        phone: ['0700000000', [Validators.required]],
      }),
    });

    this.appointmentForm.get('institution.county')?.valueChanges.subscribe((value) => {
      this.appointmentForm.get('institution.institution')?.reset();
      if (value) {
        this.newAppointmentService
          .getInstitutionsByCounty(value)
          .subscribe((res) => (this.institutions = res));
      }
    });

    // Update available times when date is selected
    this.appointmentForm.get('appointment.date')?.valueChanges.subscribe((selectedDate: Date) => {
      if (selectedDate && this.institutionDetails) {
        this.updateTimeOptions(selectedDate);
      }
    });

    // Save form data to localStorage on changes
    this.appointmentForm.valueChanges.subscribe((value) => {
      localStorage.setItem(
        'appointmentFormData',
        JSON.stringify({
          formData: value,
          currentStep: this.currentStep,
        })
      );
    });
  }

  ngOnInit(): void {
    this.route.data.subscribe(({ counties }) => {
      this.counties = counties;
    });

    // Restore form data from localStorage if available
    const savedData = localStorage.getItem('appointmentFormData');
    if (savedData) {
      try {
        const savedState = JSON.parse(savedData);
        const parsedData = savedState.formData || savedState;
        // Restore institution data first
        if (parsedData.institution?.county) {
          this.appointmentForm.patchValue({
            institution: { county: parsedData.institution.county },
          });
          // Load institutions for the saved county
          this.newAppointmentService
            .getInstitutionsByCounty(parsedData.institution.county)
            .subscribe((res) => {
              this.institutions = res;
              // Then restore the institution selection
              if (parsedData.institution?.institution) {
                this.appointmentForm.patchValue({
                  institution: { institution: parsedData.institution.institution },
                });
                // Load institution details
                const institution = res.find((i) => i.id === parsedData.institution.institution);
                if (institution) {
                  this.newAppointmentService
                    .getInstitutionDetails(String(institution.institutionType))
                    .subscribe((details) => {
                      this.institutionDetails = details;
                      // Restore appointment data
                      if (parsedData.appointment) {
                        const appointment = { ...parsedData.appointment };
                        // Convert date string back to Date object
                        if (appointment.date) {
                          appointment.date = new Date(appointment.date);
                        }
                        if (appointment.time) {
                          appointment.time = new Date(appointment.time);
                        }
                        this.appointmentForm.patchValue({ appointment });
                        // Update time options if date is set
                        if (appointment.date) {
                          this.updateTimeOptions(appointment.date);
                        }
                      }
                    });
                }
              }
            });
        }
        // Restore user data
        if (parsedData.user) {
          this.appointmentForm.patchValue({ user: parsedData.user });
        }
        // Restore current step
        if (savedState.currentStep) {
          this.currentStep = savedState.currentStep;
        }
      } catch (e) {
        console.error('Error restoring form data:', e);
        localStorage.removeItem('appointmentFormData');
      }
    }
  }

  protected onSubmit() {
    if (this.appointmentForm.invalid) {
      return;
    }

    const formValue = this.appointmentForm.value;
    const selectedInstitution = this.institutions?.find(
      (i) => i.id === formValue.institution.institution
    );
    const selectedService = this.institutionDetails?.availableServices.find(
      (s: any) => s.id === formValue.appointment.service
    );

    const appointmentRequest = {
      institutionId: formValue.institution.institution,
      institutionType: selectedInstitution?.institutionType || 'UNKNOWN',
      appointmentTime: this.combineDateAndTime(
        formValue.appointment.date,
        formValue.appointment.time
      ),
      customerName: formValue.user.name,
      customerEmail: formValue.user.email,
      customerPhone: formValue.user.phone,
      serviceType: 'ELIBERARE_CI',
      priorityLevel: 'MEDIUM',
      notes: selectedService?.name || '',
      documentRequired: '',
    };

    console.log('Sending appointment request:', appointmentRequest);

    this.newAppointmentService.saveAppointment(appointmentRequest).subscribe({
      next: (appointmentId: number) => {
        localStorage.removeItem('appointmentFormData'); // Clear saved data
        alert('Programare creata cu succes! ID: ' + appointmentId);
        this.appointmentForm.reset();
        this.router.navigate(['/user-appointments']);
      },
      error: (error: any) => {
        console.error('Error saving appointment:', error);
        alert('Eroare la crearea programarii. Va rugam incercati din nou.');
      },
    });
  }

  private combineDateAndTime(date: Date, time: Date): string {
    const combined = new Date(date);
    combined.setHours(time.getHours(), time.getMinutes(), 0, 0);
    return combined.toISOString();
  }

  protected getServicesByInstitutionType() {
    let institutionId = this.appointmentForm.get('institution.institution')?.value;
    let institution = this.institutions?.find((institution) => institution.id === institutionId);
    this.newAppointmentService
      .getInstitutionDetails(String(institution?.institutionType))
      .subscribe((res) => {
        this.institutionDetails = res;
        // Update time options for the selected date, if any
        const selectedDate = this.appointmentForm.get('appointment.date')?.value;
        if (selectedDate) {
          this.updateTimeOptions(selectedDate);
        }
      });
  }

  private updateTimeOptions(selectedDate: Date) {
    if (!this.institutionDetails) return;

    // Filter availability slots for the selected date
    const selectedDateOnly = new Date(selectedDate);
    selectedDateOnly.setHours(0, 0, 0, 0);

    this.timeOptions = this.institutionDetails.availability.filter((availability) => {
      const availabilityDate = new Date(availability);
      availabilityDate.setHours(0, 0, 0, 0);
      return availabilityDate.getTime() === selectedDateOnly.getTime();
    });
  }

  protected getSelectedCountyName(): string {
    const countyId = this.appointmentForm.get('institution.county')?.value;
    return this.counties?.find((c) => c.id === countyId)?.name || '';
  }

  protected getSelectedInstitutionName(): string {
    const institutionId = this.appointmentForm.get('institution.institution')?.value;
    return this.institutions?.find((i) => i.id === institutionId)?.name || '';
  }

  protected getSelectedServiceName(): string {
    const serviceId = this.appointmentForm.get('appointment.service')?.value;
    if (!serviceId || !this.institutionDetails?.availableServices) return '';
    const service = this.institutionDetails.availableServices.find((s: any) => s.id == serviceId);
    return service?.name || '';
  }

  protected getFormattedDate(): string {
    const date = this.appointmentForm.get('appointment.date')?.value;
    if (!date) return '';
    return new Date(date).toLocaleDateString('ro-RO', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }

  protected getFormattedTime(): string {
    const time = this.appointmentForm.get('appointment.time')?.value;
    if (!time) return '';
    const hours = ('0' + time.getHours()).slice(-2);
    const minutes = ('0' + time.getMinutes()).slice(-2);
    return hours + ':' + minutes;
  }
}
