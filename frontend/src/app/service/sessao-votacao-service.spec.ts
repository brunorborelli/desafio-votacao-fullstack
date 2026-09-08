import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { SessaoVotacao } from '../model/sessao-votacao';
import { SessaoVotacaoService } from './sessao-votacao-service';

describe('SessaoVotacaoService', () => {
  let service: SessaoVotacaoService;
  let controladorHttp: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(SessaoVotacaoService);
    controladorHttp = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controladorHttp.verify());

  it('deve consultar a sessão da pauta', () => {
    service.buscarPorPautaId(6).subscribe((sessao) => expect(sessao.id).toBe(10));

    const requisicao = controladorHttp.expectOne('/api/v1/pautas/6/sessao');
    expect(requisicao.request.method).toBe('GET');
    requisicao.flush(criarSessao());
  });

  it('deve abrir sessão com o corpo vazio para usar a duração padrão', () => {
    service.abrir(6).subscribe((sessao) => expect(sessao.pautaId).toBe(6));

    const requisicao = controladorHttp.expectOne('/api/v1/pautas/6/sessao');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual({});
    requisicao.flush(criarSessao());
  });

  it('deve abrir sessão com a duração informada', () => {
    service.abrir(6, { duracaoMinutos: 5 }).subscribe();

    const requisicao = controladorHttp.expectOne('/api/v1/pautas/6/sessao');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual({ duracaoMinutos: 5 });
    requisicao.flush(criarSessao());
  });

  function criarSessao(): SessaoVotacao {
    return {
      id: 10,
      pautaId: 6,
      dataInicio: '2026-09-07T20:00:00Z',
      dataFim: '2026-09-07T20:01:00Z'
    };
  }
});