import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import {
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';

import { ErroApi } from '../../model/erro-api';
import { Pauta } from '../../model/pauta';
import { PautaService } from '../../service/pauta-service';

@Component({
  selector: 'app-pautas-pagina',
  imports: [
    DatePipe,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    ReactiveFormsModule
  ],
  templateUrl: './pautas-page.html',
  styleUrl: './pautas-page.scss'
})
export class PautasPage implements OnInit {
  private readonly construtorFormulario = inject(NonNullableFormBuilder);
  private readonly pautaService = inject(PautaService);
  private readonly aviso = inject(MatSnackBar);

  protected readonly pautas = signal<Pauta[]>([]);
  protected readonly carregando = signal(true);
  protected readonly cadastrando = signal(false);
  protected readonly erroCarregamento = signal('');

  protected readonly formulario = this.construtorFormulario.group({
    titulo: [
      '',
      [Validators.required, Validators.maxLength(200), Validators.pattern(/\S/)]
    ],
    descricao: [
      '',
      [Validators.required, Validators.maxLength(1000), Validators.pattern(/\S/)]
    ]
  });

  ngOnInit(): void {
    this.carregarPautas();
  }

  protected carregarPautas(): void {
    this.carregando.set(true);
    this.erroCarregamento.set('');

    this.pautaService.listar()
      .pipe(finalize(() => this.carregando.set(false)))
      .subscribe({
        next: (pautas) => this.pautas.set(pautas),
        error: (erro: unknown) => this.erroCarregamento.set(
          this.obterMensagemErro(erro, 'Não foi possível carregar as pautas.')
        )
      });
  }

  protected cadastrar(): void {
    this.formulario.markAllAsTouched();
    if (this.formulario.invalid || this.cadastrando()) {
      return;
    }

    const valores = this.formulario.getRawValue();
    this.cadastrando.set(true);

    this.pautaService.cadastrar({
      titulo: valores.titulo.trim(),
      descricao: valores.descricao.trim()
    })
      .pipe(finalize(() => this.cadastrando.set(false)))
      .subscribe({
        next: (pauta) => {
          this.pautas.update((pautas) => [pauta, ...pautas]);
          this.formulario.reset();
          this.aviso.open('Pauta cadastrada com sucesso.', 'Fechar', {
            duration: 4000
          });
        },
        error: (erro: unknown) => this.aviso.open(
          this.obterMensagemErro(erro, 'Não foi possível cadastrar a pauta.'),
          'Fechar',
          { duration: 5000 }
        )
      });
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
