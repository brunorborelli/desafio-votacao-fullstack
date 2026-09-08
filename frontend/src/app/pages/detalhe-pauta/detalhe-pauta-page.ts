import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, finalize, forkJoin, of, throwError } from 'rxjs';

import { ErroApi } from '../../model/erro-api';
import { Pauta } from '../../model/pauta';
import { SessaoVotacao } from '../../model/sessao-votacao';
import { PautaService} from '../../service/pauta-service';
import { SessaoVotacaoService } from '../../service/sessao-votacao-service';

@Component({
  selector: 'app-detalhe-pauta-pagina',
  imports: [
    DatePipe,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './detalhe-pauta-page.html',
  styleUrl: './detalhe-pauta-page.scss'
})
export class DetalhePautaPage implements OnInit {
  private readonly rota = inject(ActivatedRoute);
  private readonly pautaService = inject(PautaService);
  private readonly sessaoVotacaoService = inject(SessaoVotacaoService);
  private readonly aviso = inject(MatSnackBar);

  private pautaId = 0;

  protected readonly pauta = signal<Pauta | null>(null);
  protected readonly sessao = signal<SessaoVotacao | null>(null);
  protected readonly carregando = signal(true);
  protected readonly abrindoSessao = signal(false);
  protected readonly erroCarregamento = signal('');

  protected readonly duracaoMinutos = new FormControl<number | null>(null, {
    validators: [Validators.min(1), Validators.pattern(/^\d+$/)]
  });

  ngOnInit(): void {
    const id = Number(this.rota.snapshot.paramMap.get('id'));
    if (!Number.isInteger(id) || id <= 0) {
      this.erroCarregamento.set('O identificador da pauta é inválido.');
      this.carregando.set(false);
      return;
    }

    this.pautaId = id;
    this.carregarDados();
  }

  protected carregarDados(): void {
    this.carregando.set(true);
    this.erroCarregamento.set('');

    forkJoin({
      pauta: this.pautaService.buscarPorId(this.pautaId),
      sessao: this.sessaoVotacaoService.buscarPorPautaId(this.pautaId).pipe(
        catchError((erro: unknown) => {
          if (erro instanceof HttpErrorResponse && erro.status === 404) {
            return of(null);
          }

          return throwError(() => erro);
        })
      )
    })
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: ({ pauta, sessao }) => {
          this.pauta.set(pauta);
          this.sessao.set(sessao);
        },
        error: (erro: unknown) => this.erroCarregamento.set(
          this.obterMensagemErro(erro, 'Não foi possível carregar a pauta.')
        )
      });
  }

  protected abrirSessao(): void {
    this.duracaoMinutos.markAsTouched();
    if (this.duracaoMinutos.invalid || this.abrindoSessao()) {
      return;
    }

    const duracao = this.duracaoMinutos.value;
    const requisicao = duracao === null ? undefined : { duracaoMinutos: duracao };
    this.abrindoSessao.set(true);

    this.sessaoVotacaoService.abrir(this.pautaId, requisicao)
      .pipe(finalize(() => this.abrindoSessao.set(false)))
      .subscribe({
        next: (sessao) => {
          this.sessao.set(sessao);
          this.duracaoMinutos.reset();
          this.aviso.open('Sessão de votação aberta com sucesso.', 'Fechar', {
            duration: 4000
          });
        },
        error: (erro: unknown) => {
          this.aviso.open(
            this.obterMensagemErro(erro, 'Não foi possível abrir a sessão.'),
            'Fechar',
            { duration: 5000 }
          );

          if (erro instanceof HttpErrorResponse && erro.status === 409) {
            this.carregarDados();
          }
        }
      });
  }

  protected sessaoEstaAberta(sessao: SessaoVotacao): boolean {
    return Date.parse(sessao.dataFim) > Date.now();
  }

  private obterMensagemErro(erro: unknown, mensagemPadrao: string): string {
    if (erro instanceof HttpErrorResponse) {
      const resposta = erro.error as Partial<ErroApi> | null;
      if (typeof resposta?.mensagem === 'string') {
        return resposta.mensagem;
      }
    }

    return mensagemPadrao;
  }
}