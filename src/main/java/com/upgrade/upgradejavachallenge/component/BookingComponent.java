package com.upgrade.upgradejavachallenge.component;

import com.upgrade.upgradejavachallenge.model.Reservation;
import com.upgrade.upgradejavachallenge.model.User;
import com.upgrade.upgradejavachallenge.repository.ReservationRepository;
import com.upgrade.upgradejavachallenge.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class BookingComponent {
    private ReservationRepository reservationRepository;

    private UserRepository userRepository;

    private List<Reservation> reservationList;

    @Autowired
    public BookingComponent(ReservationRepository reservationRepository,
                            UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    public List<Reservation> getAllReservations() {
        reservationList = reservationRepository.findAll();

        return reservationList;
    }


    public void removeBooking(Long id) {
        reservationRepository.deleteById(id);
    }

    public Reservation recordBooking(LocalDateTime startDate, LocalDateTime endDate, String name, String email) {
        return reservationRepository.save(new Reservation(startDate, endDate, new User(name, email)));
    }

    public Reservation recordBooking(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Optional<Reservation> findReservation(Long id) {
        return reservationRepository.findById(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

}
