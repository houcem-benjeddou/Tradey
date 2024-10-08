package com._INFINI.PI.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    Long orderId;
    int quantity;
    @Enumerated(EnumType.STRING)
    OrderType orderType;

    @ManyToOne
    Asset asset;

    @ManyToOne
    Trader trader;


}
