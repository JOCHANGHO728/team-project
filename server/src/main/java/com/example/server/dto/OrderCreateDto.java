package com.example.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class OrderCreateDto {
    @JsonProperty("login_id")
    private String uId;
    private List<OrderItemDto> items;
}