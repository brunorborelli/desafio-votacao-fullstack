export interface SessaoVotacao {
  id: number;
  pautaId: number;
  dataInicio: string;
  dataFim: string;
}

export interface AbrirSessaoVotacaoRequisicao {
  duracaoMinutos?: number;
}