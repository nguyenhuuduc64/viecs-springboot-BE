package com.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE) //cac field khong co type thi se mac dinh la private
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    String name;
    String description;
}
