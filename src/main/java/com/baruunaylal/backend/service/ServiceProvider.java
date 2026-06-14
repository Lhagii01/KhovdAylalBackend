package com.baruunaylal.backend.service;

import com.baruunaylal.backend.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "service_providers")
@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceProvider extends User {

    /**
     * [ЗАСВАР]: registrationNumber-ийн давхардсан тодорхойлолтыг арилгасан.
     * Энэ талбар нь классын дотор зөвхөн нэг удаа зарлагдах ёстой.
     */
    private String registrationNumber; // Зөвхөн энэ мөр үлдэх ёстой.

    private String serviceType;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    // ... бусад талбарууд болон функцүүд
}