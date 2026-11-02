package br.com.educacao.gamificada.recompensa.dto;
import br.com.educacao.gamificada.dto.CriarAlunoRequest;
import br.com.educacao.gamificada.dto.ConclusaoResponse;
import br.com.educacao.gamificada.dto.ConcluirCursoRequest;
import br.com.educacao.gamificada.dto.AlunoResponse;
import br.com.educacao.gamificada.recompensa.entity.RecompensaEntity;
import java.time.Instant;
public record RecompensaResponse(Long alunoId, int vouchers, int moedas, String origem, Instant registradoEm) {
    public static RecompensaResponse from(RecompensaEntity registro) {
        return new RecompensaResponse(registro.getAlunoId(), registro.getVouchers(), registro.getMoedas(), registro.getOrigem(), registro.getRegistradoEm());
    }
}
