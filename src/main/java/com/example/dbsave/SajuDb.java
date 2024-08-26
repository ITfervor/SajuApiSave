package com.example.dbsave;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SajuDb")
public class SajuDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apiId")
    private Long apiId;  //

    @Column(name="solJd")
    private String solJd;

    @Column(name = "solYear")
    private String solYear;

    @Column(name = "solMonth")
    private String solMonth;

    @Column(name = "solDay")
    private String solDay;

    @Column(name = "lunWolgeon")
    private String lunWolgeon;

    @Column(name = "lunSecha")
    private String lunSecha;

    @Column(name = "lunIljin")
    private String lunIljin;
}
