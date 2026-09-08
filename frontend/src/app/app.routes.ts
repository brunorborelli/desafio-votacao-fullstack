import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'pautas/:id',
    loadComponent: () => import('./pages/detalhe-pauta/detalhe-pauta-page')
      .then((modulo) => modulo.DetalhePautaPage),
    title: 'Detalhes da pauta | Sistema de Votação'
  },
  {
    path: 'pautas',
    loadComponent: () => import('./pages/pautas/pautas-page')
      .then((modulo) => modulo.PautasPage),
    title: 'Pautas | Sistema de Votação'
  },
  { path: '', pathMatch: 'full', redirectTo: 'pautas' },
  { path: '**', redirectTo: 'pautas' }
];
