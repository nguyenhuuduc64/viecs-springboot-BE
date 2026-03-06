package com.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Data
@Builder // Thêm Builder để tạo Object nhanh hơn thưa ông chủ
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String name;

    @Column(unique = true)
    String taxCode;

    String email;
    String phoneNumber;
    String websiteUrl;
    String address;

    // --- THÊM CÁC TRƯỜNG VỀ THƯƠNG HIỆU ---
    String logo; // Lưu URL ảnh logo
    String banner; // Lưu URL ảnh bìa công ty

    @Column(columnDefinition = "TEXT")
    String description; // Mô tả ngắn gọn


    // --- MỐI QUAN HỆ ---
    @OneToOne
    @JoinColumn(name = "user_id", unique = true) // Nên chỉ định rõ tên cột khóa ngoại
            User user;
}