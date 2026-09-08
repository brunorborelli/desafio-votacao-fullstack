import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { DetalhePautaPage } from './detalhe-pauta-page';

describe('DetalhePautaPage', () => {
  let controladorHttp: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DetalhePautaPage],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ id: '8' })
            }
          }
        }
      ]
    });

    controladorHttp = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controladorHttp.verify());

  it('deve enviar a abertura da sessão ao submeter o formulário', () => {
    const fixture = TestBed.createComponent(DetalhePautaPage);
    fixture.detectChanges();

    controladorHttp.expectOne('/api/v1/pautas/8').flush({
      id: 8,
      titulo: 'Pauta de teste',
      descricao: 'Descrição da pauta',
      dataCriacao: '2026-09-07T20:00:00Z'
    });
    controladorHttp.expectOne('/api/v1/pautas/8/sessao').flush(
      {
        status: 404,
        mensagem: 'Sessão não encontrada'
      },
      {
        status: 404,
        statusText: 'Not Found'
      }
    );
    fixture.detectChanges();

    const formulario = fixture.nativeElement.querySelector('form') as HTMLFormElement;
    formulario.dispatchEvent(new Event('submit'));

    const requisicao = controladorHttp.expectOne('/api/v1/pautas/8/sessao');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual({});
    requisicao.flush({
      id: 1,
      pautaId: 8,
      dataInicio: '2026-09-07T20:01:00Z',
      dataFim: '2026-09-07T20:02:00Z'
    });
  });
});
