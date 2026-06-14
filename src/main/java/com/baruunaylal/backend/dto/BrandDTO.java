package com.baruunaylal.backend.dto;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class BrandDTO {
    @Column(name = "sub_category")
    private String name;
    private String category;
    private Double price;
    private String subCategory;
    private String shopName;
    private String phone;
    private String description;
    private String googleMap;
    private List<MultipartFile> images; // Олон зураг хүлээж авах талбар
}