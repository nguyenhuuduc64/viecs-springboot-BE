package com.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class JobCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    List<Recruitment> recruitments;

    @CreatedDate // Tự động lấy ngày giờ khi insert
    @Column(updatable = false) // Đảm bảo không bị sửa khi update ngành nghề
    LocalDateTime createdAt;
}
