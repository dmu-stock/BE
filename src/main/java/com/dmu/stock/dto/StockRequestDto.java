package com.dmu.stock.dto;


import com.dmu.stock.entity.enums.StockType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockRequestDto {
    private String memberId;

    private String memberName;

    @NotBlank(message = "종목 코드는 필수입니다.")
    private String stockCode;

    @Positive(message = "평단가는 0보다 커야 합니다.")
    private double avgPrice;

    @Positive(message = "수량은 0보다 커야 합니다.")
    private double quantity;

    @NotNull(message = "주식 타입(KOREA/USA)은 필수입니다.")
    private StockType type;
}
