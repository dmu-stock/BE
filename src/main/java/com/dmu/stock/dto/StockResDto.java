package com.dmu.stock.dto;


import com.dmu.stock.entity.enums.StockType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Builder
public class StockResDto {
    private String stockCode;   // 저장된 종목
    private double avgPrice;    // 평단가
    private double quantity;    // 수량
    private double totalAmount; // 총 매수 금액
    private StockType type; //주식 타입

}