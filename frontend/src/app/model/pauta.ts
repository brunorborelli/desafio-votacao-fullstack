export interface Pauta {
  id: number;
  titulo: string;
  descricao: string;
  dataCriacao: string;
}

export interface CriarPautaRequisicao {
  titulo: string;
  descricao: string;
}