package com.upgrade.upgradejavachallenge.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id", updatable = false, nullable = false)
    private Long reservationId;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @ManyToOne
    private User user;

    @Version
    private Long version;

    public Reservation(LocalDateTime startDate, LocalDateTime endDate, User user) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }
}
