package com.example.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemDto {
    @JsonProperty("p_id")
    private Long pId;
    private int quantity;
}