package com.boots.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "t_tour")
@Getter
@Setter
@ToString
public class Tour implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String start;
    private String finish;
    private int price;
    private String date;
    private int count;
    @OneToOne(mappedBy = "tour")
    private TourDescription tourDescription;

    @ManyToMany(mappedBy = "tours")
    private Set<User> users;
}
