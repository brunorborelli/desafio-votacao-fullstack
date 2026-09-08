export type EscolhaVoto = 'SIM' | 'NAO';

export interface Voto {
  id: number;
  pautaId: number;
  cpfAssociado: string;
  escolha: EscolhaVoto;
  dataCriacao: string;
}

export interface RegistrarVotoRequisicao {
  cpfAssociado: string;
  escolha: EscolhaVoto;
}