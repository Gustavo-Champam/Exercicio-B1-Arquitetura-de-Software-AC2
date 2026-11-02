package br.com.educacao.gamificada.recompensa.service;
import br.com.educacao.gamificada.service.AlunoService;

import br.com.educacao.gamificada.domain.Plano;
import br.com.educacao.gamificada.recompensa.dto.RecompensaResponse;
import br.com.educacao.gamificada.recompensa.entity.RecompensaEntity;
import br.com.educacao.gamificada.event.AlunoPromovidoEvent;
import br.com.educacao.gamificada.recompensa.repository.RecompensaRepository;
import br.com.educacao.gamificada.repository.*;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class RecompensaService {
    private final AlunoRepository alunos;
    private final RecompensaRepository recompensas;
    public RecompensaService(AlunoRepository alunos, RecompensaRepository recompensas) {
        this.alunos = alunos;
        this.recompensas = recompensas;
    }

    @EventListener
    @Transactional
    public void registrar(AlunoPromovidoEvent evento) {
        var aluno = alunos.buscarParaAtualizar(evento.alunoId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
        // O bloqueio e a chave primária por aluno protegem reprocessamentos concorrentes.
        if (aluno.getPlano() == Plano.PREMIUM && !recompensas.existsById(aluno.getId())) {
            recompensas.saveAndFlush(new RecompensaEntity(aluno));
        }
    }

    @Transactional(readOnly = true)
    public List<RecompensaResponse> historico(Long id) {
        if (!alunos.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado.");
        }
        return recompensas.findById(id).stream().map(RecompensaResponse::from).toList();
    }
}
