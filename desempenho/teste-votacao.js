import http from 'k6/http';
import exec from 'k6/execution';
import { sleep } from 'k6';
import { Counter } from 'k6/metrics';

const URL_BASE = __ENV.URL_BASE || 'http://localhost:8080';
const QUANTIDADE_VOTOS = Number(__ENV.QUANTIDADE_VOTOS || 5000);
const USUARIOS_VIRTUAIS = Number(__ENV.USUARIOS_VIRTUAIS || 50);
const MAXIMO_TENTATIVAS_AUTORIZACAO = Number(
    __ENV.MAXIMO_TENTATIVAS_AUTORIZACAO || 20,
);
const DURACAO_MAXIMA = __ENV.DURACAO_MAXIMA || '30m';

const votosRegistrados = new Counter('votos_registrados');
const recusasAutorizacao = new Counter('recusas_autorizacao');
const respostasInesperadas = new Counter('respostas_inesperadas');

const respostaEsperadaVoto = http.expectedStatuses(201, 404);

export const options = {
    discardResponseBodies: true,
    scenarios: {
        votacao: {
            executor: 'shared-iterations',
            vus: USUARIOS_VIRTUAIS,
            iterations: QUANTIDADE_VOTOS,
            maxDuration: DURACAO_MAXIMA,
        },
    },
    thresholds: {
        'http_req_duration{operacao:registrar-voto}': ['p(95)<1000'],
        respostas_inesperadas: ['count==0'],
        votos_registrados: [`count>=${Math.floor(QUANTIDADE_VOTOS * 0.99)}`],
    },
};

export function setup() {
    esperarAplicacao();

    const identificadorExecucao = new Date().toISOString();
    const pauta = enviarJson(
        `${URL_BASE}/api/v1/pautas`,
        {
            titulo: `Teste de desempenho ${identificadorExecucao}`,
            descricao: 'Pauta criada automaticamente pelo teste de desempenho',
        },
    );

    if (pauta.status !== 201) {
        exec.test.abort(`Não foi possível cadastrar a pauta: HTTP ${pauta.status}`);
    }

    const pautaId = pauta.json('id');
    const sessao = enviarJson(
        `${URL_BASE}/api/v1/pautas/${pautaId}/sessao`,
        { duracaoMinutos: 1440 },
    );

    if (sessao.status !== 201) {
        exec.test.abort(`Não foi possível abrir a sessão: HTTP ${sessao.status}`);
    }

    return { pautaId };
}

export default function (dados) {
    respostasInesperadas.add(0);

    const indice = exec.scenario.iterationInTest;
    const cpfAssociado = gerarCpf(indice);
    const corpo = JSON.stringify({
        cpfAssociado,
        escolha: indice % 2 === 0 ? 'SIM' : 'NAO',
    });

    for (let tentativa = 0; tentativa < MAXIMO_TENTATIVAS_AUTORIZACAO; tentativa++) {
        const resposta = http.post(
            `${URL_BASE}/api/v1/pautas/${dados.pautaId}/votos`,
            corpo,
            {
                headers: { 'Content-Type': 'application/json' },
                responseCallback: respostaEsperadaVoto,
                tags: {
                    name: 'POST /api/v1/pautas/{pautaId}/votos',
                    operacao: 'registrar-voto',
                },
            },
        );

        if (resposta.status === 201) {
            votosRegistrados.add(1);
            return;
        }

        if (resposta.status === 404) {
            recusasAutorizacao.add(1);
            continue;
        }

        respostasInesperadas.add(1);
        return;
    }
}

function esperarAplicacao() {
    for (let tentativa = 0; tentativa < 30; tentativa++) {
        const resposta = http.get(`${URL_BASE}/v3/api-docs`, {
            tags: { operacao: 'preparacao' },
        });

        if (resposta.status === 200) {
            return;
        }

        sleep(1);
    }

    exec.test.abort('A aplicação não ficou disponível dentro de 30 segundos');
}

function enviarJson(url, dados) {
    return http.post(url, JSON.stringify(dados), {
        headers: { 'Content-Type': 'application/json' },
        responseType: 'text',
        tags: { operacao: 'preparacao' },
    });
}

function gerarCpf(indice) {
    const baseNumerica = 100000000 + (indice % 899999999);
    const base = String(baseNumerica).padStart(9, '0');
    const primeiroDigito = calcularDigitoVerificador(base);
    const segundoDigito = calcularDigitoVerificador(`${base}${primeiroDigito}`);

    return `${base}${primeiroDigito}${segundoDigito}`;
}

function calcularDigitoVerificador(digitos) {
    let soma = 0;
    let peso = digitos.length + 1;

    for (const digito of digitos) {
        soma += Number(digito) * peso;
        peso--;
    }

    const resultado = 11 - (soma % 11);
    return resultado >= 10 ? 0 : resultado;
}