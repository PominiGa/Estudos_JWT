package com.example.security.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    private String description;
}
