package com.smartprocure.modules.auth.entity;

import com.smartprocure.core.entity.BaseEntity;
import com.smartprocure.modules.auth.enums.ERole;
import jakarta.persistence.*;
import lombok.*;

/**
 * Enterprise Role Entity representing RBAC authorities.
 *
 * @author Principal Java Architect
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false, unique = true)
    private ERole name;

    @Column(length = 250)
    private String description;
}
