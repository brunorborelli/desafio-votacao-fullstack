import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { URL_BASE_API } from '../config/api';
import {
  AbrirSessaoVotacaoRequisicao,
  SessaoVotacao
} from '../model/sessao-votacao';

@Injectable({ providedIn: 'root' })
export class SessaoVotacaoService {
  private readonly http = inject(HttpClient);

  abrir(
    pautaId: number,
    requisicao?: AbrirSessaoVotacaoRequisicao
  ): Observable<SessaoVotacao> {
    return this.http.post<SessaoVotacao>(
      this.montarUrl(pautaId),
      requisicao ?? null
    );
  }

  buscarPorPautaId(pautaId: number): Observable<SessaoVotacao> {
    return this.http.get<SessaoVotacao>(this.montarUrl(pautaId));
  }

  private montarUrl(pautaId: number): string {
    return `${URL_BASE_API}/pautas/${pautaId}/sessao`;
  }
}
