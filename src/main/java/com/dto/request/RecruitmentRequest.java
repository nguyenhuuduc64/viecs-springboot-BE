package com.dto.request;

import com.entity.Company;
import jakarta.persistence.*;
import jdk.jfr.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class RecruitmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String title;

    String content;

    String salary;

    String categoryId;

}
