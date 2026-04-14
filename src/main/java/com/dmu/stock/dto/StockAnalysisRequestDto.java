package com.dmu.stock.dto;

import com.dmu.stock.entity.enums.StockType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 파이썬에게 주식 분석 요청 전, 한국주식이면 stockCode, 미국주식이면 ticker로 지정하여 request
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAnalysisRequestDto {
    private String stockCode;
    private StockType type;
}
