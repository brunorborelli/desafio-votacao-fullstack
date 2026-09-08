export interface ErroApi {
  instante: string;
  status: number;
  erro: string;
  mensagem: string;
  caminho: string;
  campos: Record<string, string>;
}