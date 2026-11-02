package br.com.educacao.gamificada.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TratamentoErrosTest {
    @Test
    void deveConverterViolacaoDeDominioEmResposta400SemStackTrace() {
        var resposta = new TratamentoErros().tratarArgumento(new IllegalArgumentException("A média deve estar entre 0 e 10."));
        assertEquals(400, resposta.getStatus());
        assertEquals("A média deve estar entre 0 e 10.", resposta.getDetail());
        assertNull(resposta.getProperties());
    }
}
