package com.entity;

import com.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecruiterRegistration{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // Ảnh giấy phép kinh doanh - Đây là thông tin quan trọng nhất để Admin check
    @Column(nullable = false)
    String businessLicenseUrl;

    // Trạng thái yêu cầu: PENDING, APPROVED, REJECTED
    @Enumerated(EnumType.STRING)
    @Builder.Default
    RequestStatus status = RequestStatus.PENDING;

    // Người yêu cầu
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    // Ngày gửi yêu cầu
    LocalDateTime createdAt;

    // Ngày Admin phê duyệt hoặc từ chối
    LocalDateTime processedAt;

    // Lý do từ chối (để báo cho người dùng sửa lại ảnh nếu mờ, sai...)
    String note;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
