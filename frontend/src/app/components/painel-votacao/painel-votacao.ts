import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  computed,
  DestroyRef,
  inject,
  input,
  signal
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { finalize, interval } from 'rxjs';

import { ErroApi } from '../../model/erro-api';
import {
  ResultadoVotacao,
  SituacaoResultadoVotacao
} from '../../model/resultado-votacao';
import { SessaoVotacao } from '../../model/sessao-votacao';
import { EscolhaVoto } from '../../model/voto';
import { VotoService } from '../../service/voto-service';

@Component({
  selector: 'app-painel-votacao',
  imports: [
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatSnackBarModule,
    ReactiveFormsModule
  ],
  templateUrl: './painel-votacao.html',
  styleUrl: './painel-votacao.scss'
})
export class PainelVotacao {
  private readonly votoService = inject(VotoService);
  private readonly aviso = inject(MatSnackBar);
  private readonly destruidor = inject(DestroyRef);
  private readonly agora = signal(Date.now());

  readonly pautaId = input.required<number>();
  readonly sessao = input.required<SessaoVotacao>();

  protected readonly registrandoVoto = signal(false);
  protected readonly carregandoResultado = signal(false);
  protected readonly resultado = signal<ResultadoVotacao | null>(null);
  protected readonly erroResultado = signal('');

  protected readonly sessaoAberta = computed(
    () => Date.parse(this.sessao().dataFim) > this.agora()
  );

  protected readonly tempoRestante = computed(() => {
    const segundos = Math.max(
      0,
      Math.ceil((Date.parse(this.sessao().dataFim) - this.agora()) / 1000)
    );
    const minutos = Math.floor(segundos / 60);
    const segundosRestantes = segundos % 60;
    return `${minutos}min ${segundosRestantes.toString().padStart(2, '0')}s`;
  });

  protected readonly formularioVoto = new FormGroup({
    cpfAssociado: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.pattern(/^(\d{11}|\d{3}\.\d{3}\.\d{3}-\d{2})$/)
      ]
    }),
    escolha: new FormControl<EscolhaVoto | null>(null, {
      validators: [Validators.required]
    })
  });

  constructor() {
    interval(1000)
      .pipe(takeUntilDestroyed(this.destruidor))
      .subscribe(() => this.agora.set(Date.now()));
  }

  protected registrarVoto(): void {
    this.formularioVoto.markAllAsTouched();
    if (this.formularioVoto.invalid || this.registrandoVoto()) {
      return;
    }

    const valores = this.formularioVoto.getRawValue();
    if (valores.escolha === null) {
      return;
    }

    this.registrandoVoto.set(true);
    this.votoService.registrar(this.pautaId(), {
      cpfAssociado: valores.cpfAssociado.replace(/\D/g, ''),
      escolha: valores.escolha
    })
      .pipe(finalize(() => this.registrandoVoto.set(false)))
      .subscribe({
        next: () => {
          this.formularioVoto.reset();
          this.aviso.open('Voto registrado com sucesso.', 'Fechar', {
            duration: 4000
          });
        },
        error: (erro: unknown) => this.aviso.open(
          this.obterMensagemErro(erro, 'Não foi possível registrar o voto.'),
          'Fechar',
          { duration: 5000 }
        )
      });
  }

  protected consultarResultado(): void {
    if (this.carregandoResultado()) {
      return;
    }

    this.carregandoResultado.set(true);
    this.erroResultado.set('');

    this.votoService.obterResultado(this.pautaId())
      .pipe(finalize(() => this.carregandoResultado.set(false)))
      .subscribe({
        next: (resultado) => this.resultado.set(resultado),
        error: (erro: unknown) => this.erroResultado.set(
          this.obterMensagemErro(erro, 'Não foi possível consultar o resultado.')
        )
      });
  }

  protected textoResultado(situacao: SituacaoResultadoVotacao): string {
    const textos: Record<SituacaoResultadoVotacao, string> = {
      APROVADA: 'Pauta aprovada',
      REPROVADA: 'Pauta reprovada',
      EMPATE: 'Votação empatada',
      SEM_VOTOS: 'Nenhum voto registrado'
    };

    return textos[situacao];
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