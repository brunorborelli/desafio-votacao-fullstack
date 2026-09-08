import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'pautas',
    loadComponent: () => import('./pages/pautas/pautas-page')
      .then((modulo) => modulo.PautasPage),
    title: 'Pautas | Sistema de Votação'
  },
  { path: '', pathMatch: 'full', redirectTo: 'pautas' },
  { path: '**', redirectTo: 'pautas' }
];
