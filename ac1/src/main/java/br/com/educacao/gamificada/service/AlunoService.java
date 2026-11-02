package br.com.educacao.gamificada.service;

import br.com.educacao.gamificada.domain.Aluno;
import br.com.educacao.gamificada.domain.Plano;
import br.com.educacao.gamificada.recompensa.dto.RecompensaResponse;
import br.com.educacao.gamificada.dto.*;
import br.com.educacao.gamificada.entity.AlunoEntity;
import br.com.educacao.gamificada.entity.ConclusaoCursoEntity;
import br.com.educacao.gamificada.event.AlunoPromovidoEvent;
import br.com.educacao.gamificada.repository.AlunoRepository;
import br.com.educacao.gamificada.repository.ConclusaoCursoRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class AlunoService {
    private final AlunoRepository alunos;
    private final ConclusaoCursoRepository conclusoes;
    private final ApplicationEventPublisher eventos;

    public AlunoService(AlunoRepository alunos, ConclusaoCursoRepository conclusoes,
                        ApplicationEventPublisher eventos) {
        this.alunos = alunos;
        this.conclusoes = conclusoes;
        this.eventos = eventos;
    }

    @Transactional
    public AlunoResponse criar(CriarAlunoRequest pedido) {
        var aluno = new Aluno(pedido.nome().strip(), Plano.BASICO, 0);
        return AlunoResponse.from(alunos.save(new AlunoEntity(aluno)));
    }

    public List<AlunoResponse> listar() {
        return alunos.findAll(Sort.by("id")).stream().map(AlunoResponse::from).toList();
    }

    public AlunoResponse buscar(Long id) {
        return AlunoResponse.from(alunos.findById(id).orElseThrow(this::alunoNaoEncontrado));
    }

    @Transactional
    public ConclusaoResponse concluirCurso(Long id, ConcluirCursoRequest pedido) {
        var entidade = alunos.buscarParaAtualizar(id).orElseThrow(this::alunoNaoEncontrado);
        String codigoCurso = pedido.codigoCurso().strip().toUpperCase(Locale.ROOT);
        if (conclusoes.existsByAluno_IdAndCodigoCurso(id, codigoCurso)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este curso já foi contabilizado para o aluno.");
        }

        var aluno = entidade.toDomain();
        int quantidadeAnterior = aluno.getCursosConcluidos();
        boolean promovido = aluno.concluirCurso(pedido.media());
        boolean contabilizado = aluno.getCursosConcluidos() > quantidadeAnterior;

        if (contabilizado) {
            entidade.atualizarProgressao(aluno);
            conclusoes.save(new ConclusaoCursoEntity(entidade, codigoCurso, pedido.media()));
            alunos.saveAndFlush(entidade);
        }
        if (promovido) {
            eventos.publishEvent(new AlunoPromovidoEvent(id));
        }
        return new ConclusaoResponse(AlunoResponse.from(entidade), contabilizado, promovido);
    }

    private ResponseStatusException alunoNaoEncontrado() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado.");
    }
}
