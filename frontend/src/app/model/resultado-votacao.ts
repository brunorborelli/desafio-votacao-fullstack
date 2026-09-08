export type SituacaoResultadoVotacao =
  | 'APROVADA'
  | 'REPROVADA'
  | 'EMPATE'
  | 'SEM_VOTOS';

export interface ResultadoVotacao {
  pautaId: number;
  titulo: string;
  quantidadeVotosSim: number;
  quantidadeVotosNao: number;
  totalVotos: number;
  resultado: SituacaoResultadoVotacao;
}