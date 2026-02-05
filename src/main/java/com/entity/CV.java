package com.entity;

import com.converter.JsonConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CV {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @Column(columnDefinition = "LONGTEXT")
    @Convert(converter = JsonConverter.class)
    private Object content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    Date createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id") // Tạo cột user_id trong bảng CV để liên kết
    User user;

    @ManyToMany
    @JoinTable(
            name = "cv_components", // Tên bảng trung gian ngắn gọn, rõ nghĩa
            joinColumns = @JoinColumn(name = "cv_id"), // Khóa ngoại trỏ về bảng CV
            inverseJoinColumns = @JoinColumn(name = "component_template_id") // Nên thêm _id thưa ông chủ
    )
    private List<CVComponentTemplate> componentTemplates;
}
