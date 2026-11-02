package br.com.educacao.gamificada.dto;

import jakarta.validation.constraints.*;

public record ConcluirCursoRequest(
        @NotBlank(message = "O código do curso é obrigatório.")
        @Size(max = 80, message = "O código do curso deve ter até 80 caracteres.") String codigoCurso,
        @NotNull(message = "A média é obrigatória.")
        @DecimalMin(value = "0.0", message = "A média mínima é 0.")
        @DecimalMax(value = "10.0", message = "A média máxima é 10.") Double media) { }
