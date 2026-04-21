package com.dmu.stock.repository;

import com.dmu.stock.entity.Member;
import com.dmu.stock.entity.UserStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberStockRepository extends JpaRepository<UserStock,Long> {

//    Optional<UserStock> findByMember_MemberId(String memberId);

//    @Query("SELECT us FROM UserStock us WHERE us.member.memberId = :memberId")
//    List<UserStock> findByMemberId(@Param("memberId") String memberId);
    List<UserStock> findByMember_Email(String email);
}
