package com.example.security.dto.request;

import com.example.security.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {

    @NotNull(message = "Status é obrigatório")
    private OrderStatus status;
}
