package com.upgrade.upgradejavachallenge.services;

import com.upgrade.upgradejavachallenge.component.BookingComponent;
import com.upgrade.upgradejavachallenge.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationService {
    private BookingComponent bookingComponent;

    @Autowired
    public ReservationService(BookingComponent bookingComponent) {
        this.bookingComponent = bookingComponent;
    }

    public List<LocalDateTime> findAvailability(LocalDate startDate, LocalDate endDate) {
        return bookingComponent.findAvailability(startDate, endDate);
    }

    public Optional<Long> reserve(LocalDate fromDate, LocalDate toDate, User user) {
        return Optional.ofNullable(bookingComponent.performBooking(fromDate, toDate, user));
    }

    public void remove(Long id) {
        bookingComponent.removeBooking(id);
    }

    public Boolean update(Long id, LocalDate fromDate, LocalDate toDate) {
        return bookingComponent.updateBooking(id, fromDate, toDate);
    }
}

