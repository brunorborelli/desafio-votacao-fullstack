import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ResultadoVotacao } from '../model/resultado-votacao';
import { Voto } from '../model/voto';
import { VotoService } from './voto-service';

describe('VotoService', () => {
  let service: VotoService;
  let controladorHttp: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(VotoService);
    controladorHttp = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controladorHttp.verify());

  it('deve registrar o voto', () => {
    const corpo = { cpfAssociado: '52998224725', escolha: 'SIM' as const };

    service.registrar(6, corpo).subscribe((voto) => expect(voto.escolha).toBe('SIM'));

    const requisicao = controladorHttp.expectOne('/api/v1/pautas/6/votos');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual(corpo);
    requisicao.flush(criarVoto());
  });

  it('deve consultar o resultado', () => {
    service.obterResultado(6).subscribe((resultado) => {
      expect(resultado.resultado).toBe('APROVADA');
      expect(resultado.totalVotos).toBe(3);
    });

    const requisicao = controladorHttp.expectOne('/api/v1/pautas/6/resultado');
    expect(requisicao.request.method).toBe('GET');
    requisicao.flush(criarResultado());
  });

  function criarVoto(): Voto {
    return {
      id: 20,
      pautaId: 6,
      cpfAssociado: '52998224725',
      escolha: 'SIM',
      dataCriacao: '2026-09-07T20:00:30Z'
    };
  }

  function criarResultado(): ResultadoVotacao {
    return {
      pautaId: 6,
      titulo: 'Nova pauta',
      quantidadeVotosSim: 2,
      quantidadeVotosNao: 1,
      totalVotos: 3,
      resultado: 'APROVADA'
    };
  }
});