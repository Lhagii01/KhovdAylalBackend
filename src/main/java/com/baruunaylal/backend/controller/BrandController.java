package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.BrandDTO;
import com.baruunaylal.backend.dto.TravelerCommentDTO;
import com.baruunaylal.backend.entity.BrandComment;
import com.baruunaylal.backend.entity.BrandProduct;
import com.baruunaylal.backend.entity.CommentReply;
import com.baruunaylal.backend.service.BrandCommentService;
import com.baruunaylal.backend.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BrandController {

    private final BrandService brandService;
    private final BrandCommentService brandCommentService;
    // Төслийн root хавтас доторх uploads/brands замыг зааж өгнө
    private final String UPLOAD_DIR = "uploads/brands/";

    @GetMapping("/products")
    public ResponseEntity<List<BrandProduct>> getAllProducts() {
        return ResponseEntity.ok(brandService.getAllProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<BrandProduct> getProductById(@PathVariable Long id) {
        try {
            BrandProduct product = brandService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/products/{id}/comments")
    public ResponseEntity<List<TravelerCommentDTO>> getProductComments(@PathVariable Long id) {
        return ResponseEntity.ok(brandCommentService.getProductComments(id));
    }

    @PostMapping(value = "/products/{id}/comments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProductComment(
            @PathVariable Long id,
            @RequestPart("comment") BrandComment comment,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            return ResponseEntity.ok(brandCommentService.saveComment(id, comment, images));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Сэтгэгдэл хадгалахад алдаа гарлаа: " + e.getMessage());
        }
    }

    @PostMapping(value = "/comments/{commentId}/replies", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addProductCommentReply(
            @PathVariable Long commentId,
            @RequestBody CommentReply reply) {
        try {
            return ResponseEntity.ok(brandCommentService.saveReply(commentId, reply));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Хариу хадгалахад алдаа гарлаа: " + e.getMessage());
        }
    }

    @GetMapping("/products/category/{category}")
    public ResponseEntity<List<BrandProduct>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(brandService.getProductsByCategory(category));
    }

    // --- БҮРТГЭХ ХЭСЭГ ---
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerBrand(@ModelAttribute BrandDTO brandDTO) {
        try {
            List<String> imageFileNames = saveImages(brandDTO.getImages());
            brandService.saveBrandWithImages(brandDTO, imageFileNames);
            return ResponseEntity.ok("Амжилттай бүртгэгдлээ");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Зураг хадгалахад алдаа гарлаа: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Алдаа: " + e.getMessage());
        }
    }

    // --- ЗАСАХ ХЭСЭГ ---
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateBrand(@PathVariable Long id, @ModelAttribute BrandDTO brandDTO) {
        try {
            List<String> newImageNames = null;
            // Хэрэв шинэ зураг ирсэн бол хадгална
            if (brandDTO.getImages() != null && !brandDTO.getImages().isEmpty()) {
                newImageNames = saveImages(brandDTO.getImages());
            }
            brandService.updateBrand(id, brandDTO, newImageNames);
            return ResponseEntity.ok("Мэдээлэл амжилттай шинэчлэгдлээ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Засахад алдаа гарлаа: " + e.getMessage());
        }
    }

    // --- УСТГАХ ХЭСЭГ ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable Long id) {
        try {
            brandService.deleteBrand(id);
            return ResponseEntity.ok("Амжилттай устгагдлаа");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Устгахад алдаа гарлаа");
        }
    }

    /**
     * Зургуудыг файл системд хадгалах функц
     */
    private List<String> saveImages(List<MultipartFile> files) throws IOException {
        List<String> fileNames = new ArrayList<>();
        Path uploadPath = Paths.get(UPLOAD_DIR);

        // Хавтас байхгүй бол үүсгэнэ
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // Файлын нэрийг давхцуулахгүйн тулд UUID ашиглана
                    String originalFileName = file.getOriginalFilename();
                    String fileName = UUID.randomUUID().toString() + "_" +
                            (originalFileName != null ? originalFileName.replace(" ", "_") : "image");

                    Path filePath = uploadPath.resolve(fileName);

                    // Файлыг хуулна
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // DB-д хадгалагдах зам (Frontend дээр "uploads/brands/нэр" гэж харагдана)
                    fileNames.add("brands/" + fileName);
                }
            }
        }
        return fileNames;
    }
}
