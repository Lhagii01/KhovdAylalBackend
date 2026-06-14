package com.baruunaylal.backend.entity;

import com.baruunaylal.backend.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity implements UserDetails {

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore // Нууц үгийг JSON хариунд хэзээ ч гаргахгүй
    @Column(nullable = false)
    private String password;

    private String phone;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @JsonIgnore
    @Column(name = "verification_code")
    private String verificationCode;

    @Builder.Default
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;

    @Column(name = "camp_id")
    private Long campId;

    // Сэтгэгдэлтэй холбох хэсэг (ЗАСВАР)
    @OneToMany(mappedBy = "user")
    @JsonIgnore // Infinite recursion-оос сэргийлж хэрэглэгч доторх сэтгэгдлүүдийн жагсаалтыг JSON-д гаргахгүй
    private List<Comment> comments;

    @Override
    @JsonIgnore // authorities талбарыг JSON болгохгүй (role.name() алдаанаас сэргийлнэ)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return Collections.emptyList();
        }
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return this.enabled != null && this.enabled;
    }
}
