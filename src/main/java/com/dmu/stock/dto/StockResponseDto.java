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
public class StockResponseDto {
    private String stockCode;   // 저장된 종목
    private BigDecimal avgPrice;    // 평단가
    private BigDecimal quantity;    // 수량
    private String totalAmount; // 총 매수 금액
//    private String message;

    public static String formatBigDecimal(BigDecimal value) {
        if (value == null) return "0";
        return value.stripTrailingZeros().toPlainString();
    }
}