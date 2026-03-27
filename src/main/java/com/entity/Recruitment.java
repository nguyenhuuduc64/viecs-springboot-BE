package com.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UUID;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String title;
    @Column(columnDefinition = "TEXT")
    String content;

    String salary;

    @ManyToOne
    @JoinColumn(name = "category_id")
    JobCategory category;

    @ManyToOne
    @JoinColumn(name = "company_id")
    Company company;

}
