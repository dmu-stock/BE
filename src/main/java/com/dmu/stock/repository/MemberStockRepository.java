package com.dmu.stock.repository;

import com.dmu.stock.entity.UserStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberStockRepository extends JpaRepository<UserStock,Long> {
}
