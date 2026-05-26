package com.example.security.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequest {

    @NotNull(message = "ID do produto é obrigatório")
    private Long productId;

    @Min(value = 1, message = "Quantidade mínima é 1")
    private int quantity = 1;
}