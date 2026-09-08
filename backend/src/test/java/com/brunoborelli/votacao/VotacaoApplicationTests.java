package com.brunoborelli.votacao;

import com.brunoborelli.votacao.entity.EscolhaVoto;
import com.brunoborelli.votacao.entity.Pauta;
import com.brunoborelli.votacao.entity.SessaoVotacao;
import com.brunoborelli.votacao.entity.Voto;
import com.brunoborelli.votacao.repository.PautaRepository;
import com.brunoborelli.votacao.repository.SessaoVotacaoRepository;
import com.brunoborelli.votacao.repository.VotoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Testcontainers
class VotacaoApplicationTests  extends IntegrationTest {

	@Container
	static final PostgreSQLContainer bancoDeDados = new PostgreSQLContainer("postgres:17-alpine")
			.withDatabaseName("votacao_teste")
			.withUsername("votacao")
			.withPassword("votacao");

	@Autowired
	private PautaRepository pautaRepository;

	@Autowired
	private SessaoVotacaoRepository sessaoVotacaoRepository;

	@Autowired
	private VotoRepository votoRepository;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void devePersistirModeloDeVotacao() {
		Pauta pauta = pautaRepository.saveAndFlush(
				new Pauta("Melhoria na área de lazer", "Proposta de reforma da área de lazer")
		);

		Instant inicio = Instant.now();
		SessaoVotacao sessao = sessaoVotacaoRepository.saveAndFlush(
				new SessaoVotacao(pauta, inicio, inicio.plusSeconds(60))
		);

		Voto voto = votoRepository.saveAndFlush(
				new Voto(pauta, "52998224725", EscolhaVoto.SIM)
		);

		assertThat(pauta.getId()).isNotNull();
		assertThat(sessao.getId()).isNotNull();
		assertThat(voto.getId()).isNotNull();
	}

}
