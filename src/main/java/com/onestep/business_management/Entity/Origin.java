package com.onestep.business_management.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Origins")
public class Origin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer originId;
    @Column(name = "originName")
    private String originName;
}
