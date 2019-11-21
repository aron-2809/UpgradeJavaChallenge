package com.upgrade.upgradejavachallenge.repository;

import com.upgrade.upgradejavachallenge.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
