package com.dmu.stock.service;

import com.dmu.stock.client.fastapi.FastApiClient;
import com.dmu.stock.dto.RagMyStockRequestDto;
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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberStockService {
    private final MemberStockRepository memberStockRepository;
    private final MemberRepository memberRepository;
    private final AnalyzeNewsService analyzeNewsService;
    private final FastApiClient fastApiClient;

    /**
     * member별 관심종목 등록
     * @param request
     * @return
     */
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
                .member(member)
                .build();
        UserStock saveStock = memberStockRepository.save(userStock);

        return StockResponseDto.builder()
                .stockCode(saveStock.getStockCode())
                .avgPrice(saveStock.getAvgPrice())
                .quantity(saveStock.getQuantity())
                .totalAmount(saveStock.getAvgPrice().multiply(saveStock.getQuantity().setScale(2,RoundingMode.HALF_UP)).stripTrailingZeros().toPlainString())
                .build();
    }

    /**
     * 관심 종목 조회
     * @param memberId
     * @return
     */
    @Transactional
    public List<StockResponseDto> getMemberStock(String memberId){
        List<UserStock> getStock = memberStockRepository.findByMemberId(memberId);

        return getStock.stream()
                .map(stock -> StockResponseDto.builder()
                        .stockCode(stock.getStockCode())
                        .avgPrice(stock.getAvgPrice())
                        .quantity(stock.getQuantity())
                        .totalAmount(stock.getAvgPrice().multiply(stock.getQuantity().setScale(2,RoundingMode.HALF_UP)).stripTrailingZeros().toPlainString())
                        .build())
                .toList();
    }

    @Transactional
    public Mono<String> getMyStockAnalysis(String memberId){
        // 2. FastAPI와 비동기 통신
        return Mono.fromCallable(() -> {
        List<StockResponseDto> memberStock = getMemberStock(memberId);
                    System.out.println("======= 내 주식 리스트 확인 =======");
                    memberStock.forEach(stock -> {
                        System.out.println("종목명/코드: " + stock.getStockCode() +
                                ", 평단가: " + stock.getAvgPrice() +
                                ", 수량: " + stock.getQuantity() +
                                ", 총액: " + stock.getTotalAmount());
                    });
                    System.out.println("===============================");
        List<String> list = memberStock.stream()
                .map(StockResponseDto::getStockCode)
                .toList();

        List<String> newsForRag = analyzeNewsService.getNewsByName(list);

        return RagMyStockRequestDto.builder()
                .memberStock(memberStock)
                .newsForRag(newsForRag)
                .build();
        }).subscribeOn(Schedulers.boundedElastic())
                .flatMap(fastApiClient::analyzeMyStock
                );
    }
}
