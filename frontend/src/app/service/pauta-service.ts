import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { URL_BASE_API } from '../config/api';
import { CriarPautaRequisicao, Pauta } from '../model/pauta';

@Injectable({ providedIn: 'root' })
export class PautaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${URL_BASE_API}/pautas`;

  cadastrar(requisicao: CriarPautaRequisicao): Observable<Pauta> {
    return this.http.post<Pauta>(this.url, requisicao);
  }

  listar(): Observable<Pauta[]> {
    return this.http.get<Pauta[]>(this.url);
  }

  buscarPorId(id: number): Observable<Pauta> {
    return this.http.get<Pauta>(`${this.url}/${id}`);
  }
}
