import {Routes} from '@angular/router';
import {countiesResolver} from './new-appointment/counties-resolver';
import {App} from './app';
import {Home} from './home/home';

export const routes: Routes = [

  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },

  {
    path: 'home',
    component: Home,
  },

  {
    path: 'new-appointment',
    loadComponent: () => import('./new-appointment/new-appointment').then(m => m.NewAppointment),
    resolve: {counties: countiesResolver},

  }

];
