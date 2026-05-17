package com.example.security.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {
    @NotBlank
    private String name;
    private String description;
    private String category;
    private String subcategory;
    @NotBlank
    private String EAN;
    @NotNull
    private BigDecimal price;
}