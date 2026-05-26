package com.example.security.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    private String description;
    private String category;
    private String subcategory;

    @NotBlank(message = "EAN é obrigatório")
    private String EAN;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")
    private BigDecimal price;

    @Min(value = 0, message = "Estoque não pode ser negativo")
    private Integer stockQuantity = 0;

    private String imageUrl;
}
