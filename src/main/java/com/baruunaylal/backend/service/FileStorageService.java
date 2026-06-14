package com.baruunaylal.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

/**
 * Файл хадгалах болон удирдах үндсэн интерфейс.
 */
public interface FileStorageService {

    /**
     * Файлыг хадгалах.
     * @param file Хэрэглэгчээс ирж буй файл
     * @return Хадгалагдсан файлын зам (бүрэн зам эсвэл харьцангуй зам)
     */
    String storeFile(MultipartFile file);

    /**
     * Файлын замыг ашиглан файлыг Resource хэлбэрээр дуудах (эндээс хэрэглэгч татаж авна).
     * @param filename Хадгалагдсан файлын нэр эсвэл зам
     * @return Resource (файлын өгөгдөл)
     */
    Resource loadFileAsResource(String filename);
}