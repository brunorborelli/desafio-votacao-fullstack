import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { Pauta } from '../model/pauta';
import { PautaService } from './pauta-service';

describe('PautaService', () => {
  let service: PautaService;
  let controladorHttp: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(PautaService);
    controladorHttp = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controladorHttp.verify());

  it('deve listar as pautas', () => {
    const resposta: Pauta[] = [criarPauta()];

    service.listar().subscribe((pautas) => expect(pautas).toEqual(resposta));

    const requisicao = controladorHttp.expectOne('/api/v1/pautas');
    expect(requisicao.request.method).toBe('GET');
    requisicao.flush(resposta);
  });

  it('deve cadastrar uma pauta', () => {
    const corpo = { titulo: 'Nova pauta', descricao: 'Descrição da pauta' };

    service.cadastrar(corpo).subscribe((pauta) => expect(pauta.id).toBe(1));

    const requisicao = controladorHttp.expectOne('/api/v1/pautas');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual(corpo);
    requisicao.flush(criarPauta());
  });

  it('deve buscar uma pauta pelo id', () => {
    service.buscarPorId(1).subscribe((pauta) => expect(pauta.id).toBe(1));

    const requisicao = controladorHttp.expectOne('/api/v1/pautas/1');
    expect(requisicao.request.method).toBe('GET');
    requisicao.flush(criarPauta());
  });

  function criarPauta(): Pauta {
    return {
      id: 1,
      titulo: 'Nova pauta',
      descricao: 'Descrição da pauta',
      dataCriacao: '2026-09-07T20:00:00Z'
    };
  }
});
