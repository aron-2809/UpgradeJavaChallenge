package com.upgrade.upgradejavachallenge.component;

import com.upgrade.upgradejavachallenge.model.Reservation;
import com.upgrade.upgradejavachallenge.model.User;
import com.upgrade.upgradejavachallenge.repository.ReservationRepository;
import com.upgrade.upgradejavachallenge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BookingComponent {
    private ReservationRepository reservationRepository;

    private UserRepository userRepository;

    private List<Reservation> reservationList = new ArrayList<>();

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

    public Reservation recordBooking(LocalDateTime startDate, LocalDateTime endDate,
                                     String name, String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        final User user;

        final Reservation reservation;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();

            reservation = reservationRepository.save(new Reservation(startDate, endDate, user));

        } else {
            user = userRepository.save(new User(name, email));

            reservation = reservationRepository.save(new Reservation(startDate, endDate, user));
        }

        return reservation;
    }

    public Reservation recordBooking(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Optional<Reservation> findReservation(Long id) {
        return reservationRepository.findById(id);
    }
}
