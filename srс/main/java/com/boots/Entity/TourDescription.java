package com.boots.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "t_description")
@Getter
@Setter
@ToString
public class TourDescription implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String img;
    private String text;
    @OneToOne(fetch = FetchType.EAGER)
    private Tour tour;
}
