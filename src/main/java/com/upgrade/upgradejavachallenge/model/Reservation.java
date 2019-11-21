package com.upgrade.upgradejavachallenge.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@EqualsAndHashCode
@Entity
public class Reservation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate createdDate;

    @ManyToOne(cascade = {CascadeType.ALL})
    private User user;

    public Reservation(LocalDate startDate, LocalDate endDate, User user) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }

    @PrePersist
    void createdAt() {
        this.createdDate = LocalDate.now();
    }
}
