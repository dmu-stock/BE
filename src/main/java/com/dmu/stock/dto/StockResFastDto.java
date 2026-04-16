package com.dmu.stock.dto;

import com.dmu.stock.entity.enums.StockType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockResFastDto {
    private String stockCode;   // 저장된 종목
    private double avgPrice;    // 평단가
    private double quantity;    // 수량
    private double totalAmount; // 총 매수 금액
    private StockType type; //주식 타입

    // --- 한투에서 받아온 실시간 데이터 필드 추가 ---
    private double currentPrice;   // stck_prpr 현재가
    private double changePrice;    // prdy_vrss 전일 대비
    private double changeRate;     // prdy_ctrt 등락률
    private long marketCap;        // hts_avls 시가총액
}
