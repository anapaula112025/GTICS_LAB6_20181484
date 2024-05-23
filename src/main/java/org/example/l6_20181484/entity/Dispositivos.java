package org.example.l6_20181484.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "dispositivos")
public class Dispositivos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddispositivos")
    private int id;
    @Column(name = "nombre", nullable = false)
    private String nombre;
    private int cantidad;
    private int disponibles;


}
