package com.upgrade.upgradejavachallenge.services;

import com.upgrade.upgradejavachallenge.model.Reservation;
import com.upgrade.upgradejavachallenge.model.User;
import com.upgrade.upgradejavachallenge.repository.ReservationRepository;
import com.upgrade.upgradejavachallenge.repository.UserRepository;
import com.upgrade.upgradejavachallenge.util.DateRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private ReservationRepository reservationRepository;

    private UserRepository userRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    public List<Date> getAvailability(Date startDate, Date endDate) {
        List<Reservation> reservations = reservationRepository.findAll();

//        return reservations.stream()
//                .filter(reservation -> {
//                    reservation.getStartDate().isAfter(startDate)) &&
//                    reservation.getEndDate().isBefore(endDate)
//                })
//                .collect(Collectors.toList());

        return null;
    }

    public Optional<Long> reserve(DateRange dateRange, User user) {
        //TODO: Logic to validate reservation can be made or not

        Reservation reservation = reservationRepository.save(
                new Reservation(dateRange.getStartDate(), dateRange.getEndDate(), user)
        );

        return Optional.ofNullable(reservation.getReservationId());
    }


}
