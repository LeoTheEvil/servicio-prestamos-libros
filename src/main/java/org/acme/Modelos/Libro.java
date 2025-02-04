package org.acme.Modelos;

import jakarta.persistence.*;

@Entity
@Table
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private String title;
    @Column(nullable=false)
    private String author;
    @Column(nullable=false)
    private String genre;
}