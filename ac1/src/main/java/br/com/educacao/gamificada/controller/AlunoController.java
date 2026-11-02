package br.com.educacao.gamificada.controller;

import br.com.educacao.gamificada.recompensa.dto.RecompensaResponse;
import br.com.educacao.gamificada.dto.*;
import br.com.educacao.gamificada.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Alunos", description = "US01 — Progressão de Básico para Premium")
public class AlunoController {
    private final AlunoService servico;
    private final br.com.educacao.gamificada.recompensa.service.RecompensaService recompensas;

    public AlunoController(AlunoService servico, br.com.educacao.gamificada.recompensa.service.RecompensaService recompensas) {
        this.servico = servico;
        this.recompensas = recompensas;
    }

    @GetMapping("/{id}/recompensas")
    @Operation(summary = "Consultar histórico dos prêmios recebidos na promoção")
    public List<RecompensaResponse> historico(@PathVariable Long id) { return recompensas.historico(id); }

    @PostMapping
    @Operation(summary = "Cadastrar aluno no plano Básico, com zero cursos")
    public ResponseEntity<AlunoResponse> criar(@Valid @RequestBody CriarAlunoRequest pedido) {
        var aluno = servico.criar(pedido);
        return ResponseEntity.created(URI.create("/api/alunos/" + aluno.id())).body(aluno);
    }

    @GetMapping
    @Operation(summary = "Listar alunos e seus planos")
    public List<AlunoResponse> listar() { return servico.listar(); }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar a progressão de um aluno")
    public AlunoResponse buscar(@PathVariable Long id) { return servico.buscar(id); }

    @PostMapping("/{id}/conclusoes")
    @Operation(summary = "Registrar curso concluído e avaliar a promoção para Premium")
    public ConclusaoResponse concluir(@PathVariable Long id, @Valid @RequestBody ConcluirCursoRequest pedido) {
        return servico.concluirCurso(id, pedido);
    }
}
