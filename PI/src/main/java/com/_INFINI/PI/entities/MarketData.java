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
public class MarketData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    Long marketDataId;

    @OneToMany
    Set<Asset> assets;
}
