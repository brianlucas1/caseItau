package br.com.itau.dto;



import java.time.LocalDateTime;

public record ErroResponseDTO(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {}