package com.dmu.stock.service;

import com.dmu.stock.dto.StockRequestDto;
import com.dmu.stock.dto.StockResponseDto;
import com.dmu.stock.entity.Member;
import com.dmu.stock.entity.UserStock;
import com.dmu.stock.exception.CustomException;
import com.dmu.stock.exception.ErrorType;
import com.dmu.stock.repository.MemberRepository;
import com.dmu.stock.repository.MemberStockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberStockService {
    private final MemberStockRepository memberStockRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public StockResponseDto saveMemberStock(StockRequestDto request){
        //유저 아이디로 유저 찾고 없으면 새로 등록
        Member member = memberRepository.findByMemberId(request.getMemberId())
                .orElseGet(() -> {
                    try{
                        Member newMember = Member.builder()
                                .memberId(request.getMemberId())
                                .memberName(request.getMemberName())
                                .build();
                        return memberRepository.save(newMember);
                    } catch (Exception e) {
                        throw new CustomException(ErrorType.DATABASE_ERROR);
                    }
                });
        UserStock userStock = UserStock.builder()
                .stockCode(request.getStockCode())
                .avgPrice(request.getAvgPrice())
                .quantity(request.getQuantity())
                .build();
        UserStock saveStock = memberStockRepository.save(userStock);

        return StockResponseDto.builder()
                .id(saveStock.getId())
                .stockCode(saveStock.getStockCode())
                .avgPrice(saveStock.getAvgPrice())
                .quantity(saveStock.getQuantity())
                .totalAmount(saveStock.getAvgPrice() * saveStock.getQuantity())
                .message("종목 등록이 완료되었습니다.")
                .build();
    }
}
