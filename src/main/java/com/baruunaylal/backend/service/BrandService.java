package com.baruunaylal.backend.service;

import com.baruunaylal.backend.dto.BrandDTO;
import com.baruunaylal.backend.entity.BrandProduct;
import com.baruunaylal.backend.entity.Shop;
import com.baruunaylal.backend.repository.BrandProductRepository;
import com.baruunaylal.backend.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandProductRepository productRepository;
    private final ShopRepository shopRepository;

    public List<BrandProduct> getAllProducts() {
        return productRepository.findAll();
    }

    public BrandProduct getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Брэнд олдсонгүй: " + id));
    }

    public List<BrandProduct> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Transactional
    public void saveBrandWithImages(BrandDTO dto, List<String> images) {
        BrandProduct product = new BrandProduct();
        // Шинэ бүтээгдэхүүн бүртгэхдээ дэд ангиллыг оноож өгнө
        updateProductDetails(product, dto, images);

        if (dto.getShopName() != null && !dto.getShopName().isEmpty()) {
            Shop shop = getOrCreateShop(dto);
            product.setShop(shop);
        }
        productRepository.save(product);
    }

    /**
     * Брэндийн мэдээллийг шинэчлэх ба хэрэв шинэ зураг ирсэн бол
     * устгах шаардлагатай хуучин зургуудын жагсаалтыг буцаана.
     */
    @Transactional
    public List<String> updateBrand(Long id, BrandDTO dto, List<String> newImages) {
        BrandProduct product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Брэнд олдсонгүй"));

        List<String> oldImagesToDelete = new ArrayList<>();

        // Хэрэв шинэ зургууд сонгогдсон бол хуучин зургуудыг устгах жагсаалтад авна
        if (newImages != null && !newImages.isEmpty()) {
            if (product.getImages() != null) {
                oldImagesToDelete.addAll(product.getImages());
            }
            product.setImages(newImages);
        }

        // Мэдээллийг шинэчлэх (Дэд ангилал нэмэгдсэн)
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setSubCategory(dto.getSubCategory()); // <--- Засах үед дэд ангиллыг хадгалах хэсэг
        product.setPrice(dto.getPrice());
        product.setPhone(dto.getPhone());
        product.setDescription(dto.getDescription());
        product.setGoogleMap(dto.getGoogleMap());

        if (dto.getShopName() != null && !dto.getShopName().isEmpty()) {
            Shop shop = getOrCreateShop(dto);
            product.setShop(shop);
        }

        productRepository.save(product);
        return oldImagesToDelete;
    }

    /**
     * Брэндийг устгах ба түүнд холбоотой зургуудын замыг буцаана.
     */
    @Transactional
    public List<String> deleteBrand(Long id) {
        BrandProduct product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Брэнд олдсонгүй"));

        List<String> imagesToDelete = new ArrayList<>(product.getImages());
        productRepository.delete(product);

        return imagesToDelete;
    }

    // --- Туслах функцүүд ---

    private void updateProductDetails(BrandProduct product, BrandDTO dto, List<String> images) {
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setSubCategory(dto.getSubCategory()); // <--- Бүртгэх үед дэд ангиллыг хадгалах хэс
        product.setPrice(dto.getPrice());
        product.setPhone(dto.getPhone());
        product.setDescription(dto.getDescription());
        product.setGoogleMap(dto.getGoogleMap());

        if (images != null && !images.isEmpty()) {
            product.setImages(images);
        }
    }

    private Shop getOrCreateShop(BrandDTO dto) {
        return shopRepository.findByName(dto.getShopName())
                .orElseGet(() -> {
                    Shop newShop = new Shop();
                    newShop.setName(dto.getShopName());
                    newShop.setAddress(dto.getGoogleMap());
                    return shopRepository.save(newShop);
                });
    }
}