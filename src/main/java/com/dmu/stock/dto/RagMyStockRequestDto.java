package com.dmu.stock.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RagMyStockRequestDto {
    private List<StockResFastDto> memberStock;

}
