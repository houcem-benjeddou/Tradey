package com._INFINI.PI.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Trader implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    Long traderId;

    @OneToOne
    Portfolio portfolio;

    @OneToMany(mappedBy = "trader")
    Set<Order> orders;
}
