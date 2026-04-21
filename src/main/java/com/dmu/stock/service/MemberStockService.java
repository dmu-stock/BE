package com.dmu.stock.service;

import com.dmu.stock.client.fastapi.FastApiClient;
import com.dmu.stock.client.hantu.HantuClient;
import com.dmu.stock.client.hantu.HantuDto;
import com.dmu.stock.dto.RagMyStockRequestDto;
import com.dmu.stock.dto.StockRequestDto;
import com.dmu.stock.dto.StockResDto;
import com.dmu.stock.dto.StockResFastDto;
import com.dmu.stock.entity.Member;
import com.dmu.stock.entity.UserStock;
import com.dmu.stock.entity.enums.StockType;
import com.dmu.stock.exception.CustomException;
import com.dmu.stock.exception.ErrorType;
import com.dmu.stock.repository.MemberRepository;
import com.dmu.stock.repository.MemberStockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberStockService {
    private final MemberStockRepository memberStockRepository;
    private final MemberRepository memberRepository;
    private final FastApiClient fastApiClient;
    private final HantuClient hantuClient;

    /**
     * member별 관심종목 등록
     * @param request
     * @return
     */
    @Transactional
    public StockResDto saveMemberStock(StockRequestDto request){
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorType.MEMBER_NOT_FOUND));

        UserStock userStock = UserStock.builder()
                .stockCode(request.getStockCode())
                .avgPrice(request.getAvgPrice())
                .quantity(request.getQuantity())
                .type(StockType.detectType(request.getStockCode()))
                .member(member)
                .build();
        UserStock saveStock = memberStockRepository.save(userStock);

        return StockResDto.builder()
                .stockCode(saveStock.getStockCode())
                .avgPrice(saveStock.getAvgPrice())
                .quantity(saveStock.getQuantity())
                .totalAmount(saveStock.getAvgPrice())
                .build();
    }

    /**
     * 관심 종목 조회
     * @param email
     * @return
     */
    @Transactional
    public List<StockResDto> getMemberStock(String email){
        Member member = memberRepository.findByEmail(email).orElseThrow();
        List<UserStock> getStock = member.getUserStocks();

        return getStock.stream()
                .map(stock -> StockResDto.builder()
                        .stockCode(stock.getStockCode())
                        .avgPrice(stock.getAvgPrice())
                        .quantity(stock.getQuantity())
                        .totalAmount(stock.getAvgPrice())
                        .type(StockType.detectType(stock.getStockCode()))
                        .build())
                .toList();
    }

    /**
     * 내 주식 rag 분석 요청(내 주식 정보 + 해당 회사 뉴스 데이터)
     * 동기식 메서드를 비동기 래퍼로 감싸는 작업
     * @param memberId
     * @return
     */
    @Transactional
    public Mono<String> getMyStockAnalysis(String memberId){
        // FastAPI와 비동기 통신
        return Mono.fromCallable(() -> {
            List<StockResDto> memberStock = getMemberStock(memberId);
                    List<StockResFastDto> fastDtoList = memberStock.stream()
                            .map(stock -> {

                                HantuDto.PriceResponse priceInfo = hantuClient.getStockPrice(stock.getStockCode());

                                return StockResFastDto.builder()
                                        .stockCode(stock.getStockCode())
                                        .avgPrice(stock.getAvgPrice())
                                        .quantity(stock.getQuantity())
                                        .type(StockType.detectType(stock.getStockCode()))
                                        .currentPrice(priceInfo.getOutput().getNumericPrice())
                                        .changePrice(priceInfo.getOutput().getNumericChange())
                                        .changeRate(priceInfo.getOutput().getNumericRate())
                                        .marketCap(priceInfo.getOutput().getNumericMarketCap())
                                        .build();
                            })
                            .toList();

        return RagMyStockRequestDto.builder()
                .memberStock(fastDtoList)
                .build();
        //리소스 병목 현상 방지 :
        }).subscribeOn(Schedulers.boundedElastic())
                .flatMap(fastApiClient::analyzeMyStock
                );
    }
}
