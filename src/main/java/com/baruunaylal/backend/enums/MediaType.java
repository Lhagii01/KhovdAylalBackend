package com.baruunaylal.backend.enums;
/**
 * Хадгалагдах медиа файлын үндсэн төрлийг тодорхойлно.
 *
 * IMAGE - Зураг (.jpg, .png, .webp)
 * VIDEO - Видео (.mp4, .mov)
 * DOCUMENT - Баримт бичиг (.pdf, .txt)
 * OTHER - Бусад, тодорхойгүй төрөл
 */
public enum MediaType {
    IMAGE,
    VIDEO,
    DOCUMENT,
    OTHER // 👈 Энэ утга таны MediaController-т шаардлагатай байгаа.
}