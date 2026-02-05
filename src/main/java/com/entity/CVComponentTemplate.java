package com.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CVComponentTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String componentType;
    @Column(columnDefinition = "TEXT")
    String data; // chứa chuỗi jsx của component

}
