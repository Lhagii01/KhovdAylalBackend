package com.baruunaylal.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. Төслийн үндсэн хавтас доторх 'uploads' хавтасны замыг авах
        Path rootPath = Paths.get("uploads").toAbsolutePath();
        String uploadPath = rootPath.toString();

        // 2. Хавтас байхгүй бол үүсгэх (Энэ нь алдаанаас сэргийлнэ)
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 3. Resource location-ийг стандарт хэлбэрт оруулах
        // Windows дээр "file:C:/path/", Linux дээр "file:/path/" гэж зөв хөрвүүлнэ
        String resourceLocation = "file:" + uploadPath.replace("\\", "/") + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(0);

        // Консол дээр мэдээлэл хэвлэх
        System.out.println("\n🚀 Static Resource Handler тохируулагдлаа:");
        System.out.println("📍 Absolute Path: " + uploadPath);
        System.out.println("🔗 Resource Location: " + resourceLocation);
    }
}