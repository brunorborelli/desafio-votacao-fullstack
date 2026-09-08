import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { URL_BASE_API } from '../config/api';
import { ResultadoVotacao } from '../model/resultado-votacao';
import { RegistrarVotoRequisicao, Voto } from '../model/voto';

@Injectable({ providedIn: 'root' })
export class VotoService {
  private readonly http = inject(HttpClient);

  registrar(
    pautaId: number,
    requisicao: RegistrarVotoRequisicao
  ): Observable<Voto> {
    return this.http.post<Voto>(
      `${URL_BASE_API}/pautas/${pautaId}/votos`,
      requisicao
    );
  }

  obterResultado(pautaId: number): Observable<ResultadoVotacao> {
    return this.http.get<ResultadoVotacao>(
      `${URL_BASE_API}/pautas/${pautaId}/resultado`
    );
  }
}
