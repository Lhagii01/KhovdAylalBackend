package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.BrandProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BrandProductRepository extends JpaRepository<BrandProduct, Long> {
    // Ангилалаар нь шүүж харах (Хүнс, Бэлэг дурсгал г.м)
    List<BrandProduct> findByCategory(String category);
}

