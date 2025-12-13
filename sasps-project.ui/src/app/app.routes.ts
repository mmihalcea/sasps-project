import { Routes } from '@angular/router';
import { countiesResolver } from './new-appointment/counties-resolver';
import { App } from './app';
import { Home } from './home/home';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },

  {
    path: 'home',
    component: Home,
  },

  {
    path: 'new-appointment',
    loadComponent: () => import('./new-appointment/new-appointment').then((m) => m.NewAppointment),
    resolve: { counties: countiesResolver },
  },

  {
    path: 'login',
    loadComponent: () => import('./login/login').then((m) => m.LoginComponent),
  },

  {
    path: 'admin-notifications',
    loadComponent: () =>
      import('./admin-notifications/admin-notifications').then(
        (m) => m.AdminNotificationsComponent
      ),
  },

  {
    path: 'user-appointments',
    loadComponent: () =>
      import('./user-appointments/user-appointments').then((m) => m.UserAppointmentsComponent),
  },
];
