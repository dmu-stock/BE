package com.dmu.stock.entity.enums;

public enum StockType {
    KOREA,
    USA,
    UNKNOWN;

    public static StockType detectType(String ticker){
        if (ticker == null || ticker.isBlank()) {
            return UNKNOWN;
        }
        // 6자리 숫자면 한국 주식
        if (ticker.matches("^\\d{6}$")) {
            return KOREA;
        }

        // 영문자 1~5자리면 미국 주식
        if (ticker.matches("^[A-Z]{1,5}$")) {
            return USA;
        }

        return UNKNOWN;
    }
}
