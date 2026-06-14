package com.baruunaylal.backend.service;





import com.baruunaylal.backend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

/**
 * Файлыг серверийн локал диск дээр хадгалах үйлчилгээ.
 */
@Service
public class LocalStorageService  implements FileStorageService {

    // application.properties файлаас файлуудыг хадгалах үндсэн замыг уншиж авна.
    private final Path fileStorageLocation;

    public LocalStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        // uploads хавтас үүсгэх (Хэрэв байхгүй бол)
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * Файлыг хадгалах функц.
     * @param file Хэрэглэгчээс ирж буй файл
     * @return Хадгалагдсан файлын нэр
     */
    @Override
    public String storeFile(MultipartFile file) {
        // Файлын нэрний цэвэрлэгээ
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = "";
        String fileNameWithoutExt = originalFilename;

        // Файлын өргөтгөлийг салгах
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFilename.substring(dotIndex);
            fileNameWithoutExt = originalFilename.substring(0, dotIndex);
        }

        // Файлын давхардалтыг шийдэхийн тулд UUID ашиглан шинэ нэр өгөх
        // Жишээ: original-name.jpg -> 9a4b3c2d-1e5f-4a6b-8c9d-0f1e2d3c4b5a.jpg
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // Файлын нэрэнд хүчингүй тэмдэгт байгаа эсэхийг шалгах
            if (uniqueFileName.contains("..")) {
                throw new IOException("Sorry! Filename contains invalid path sequence " + uniqueFileName);
            }

            // Файлыг зорилтот байршилд хуулах
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName; // Хадгалагдсан файлын нэрийг буцаана.
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }

    /**
     * Файлыг уншиж Resource хэлбэрээр буцаах функц.
     */
    @Override
    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                // Файл олдсонгүй тохиолдолд
                throw new RuntimeException("File not found " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + filename, ex);
        }
    }
}
