package com.dmu.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주식코드 (ticker, stock코드 구분 없이),
 * isOverseas 필드로 구분(true = 해외주식)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeStockRequestDto {
    private String stock;
}
