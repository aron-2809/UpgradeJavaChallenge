package com.upgrade.upgradejavachallenge.services;

import com.upgrade.upgradejavachallenge.component.BookingComponent;
import com.upgrade.upgradejavachallenge.model.Reservation;
import com.upgrade.upgradejavachallenge.model.User;
import com.upgrade.upgradejavachallenge.util.DateRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ReservationService {
    private BookingComponent bookingComponent;

    @Value("${campsite.advanced-booking.max-limit-months}")
    private Integer bookingMaxLimit;
    @Value("${campsite.advanced-booking.min-limit-days}")
    private Integer bookingMinLimit;
    @Value("${campsite.booking.reservation-limit-days}")
    private Integer reservationLimit;

    @Autowired
    public ReservationService(BookingComponent bookingComponent) {
        this.bookingComponent = bookingComponent;
    }

    public List<LocalDateTime> findAvailability(LocalDate startDate, LocalDate endDate) {
        LocalDateTime dateTime1 = startDate.atTime(12, 00);
        LocalDateTime dateTime2 = endDate.atTime(12, 00);

        dateTime2 = dateTime1.plusMonths(bookingMaxLimit).isBefore(dateTime2) ?
                dateTime1.plusMonths(bookingMaxLimit) : dateTime2;

        List<Reservation> reservationList = bookingComponent.getAllReservations();

        List<LocalDateTime> requestedTravelDates = new DateRange(dateTime1, dateTime2).toList();

        if (reservationList.size() > 0) {
            List<LocalDateTime> reservationDates = new ArrayList<>();

            List<DateRange> reservationDateRanges = reservationList.stream()
                    .map(reservation -> new DateRange(reservation.getStartDate(),
                            reservation.getEndDate()))
                    .collect(Collectors.toList());

            reservationDateRanges.stream()
                    .forEach(reserveDateRange -> reservationDates.addAll(reserveDateRange.toList()));

            requestedTravelDates.removeAll(reservationDates);
        }

        return requestedTravelDates;

    }

    private Boolean advancedBookingValidations(LocalDateTime date1) {
        return date1.minusMonths(bookingMaxLimit).isBefore(LocalDateTime.now()) &&
                date1.minusDays(bookingMinLimit).isAfter(LocalDateTime.now());
    }

    public Long reserve(LocalDate startDate, LocalDate endDate, User user) {
        LocalDateTime dateTime1 = startDate.atTime(12, 00);
        LocalDateTime dateTime2 = endDate.atTime(12, 00);

        List<User> existingUsers = bookingComponent.findAllUsers();

        if (!existingUsers.isEmpty()) {
            Optional<User> optionalUser = existingUsers.stream()
                    .filter(usr -> usr.getEmail().equals(user.getEmail()))
                    .findFirst();

            if (optionalUser.isPresent()) {
                user.setUserId(optionalUser.get().getUserId());
            }
        }

        if (advancedBookingValidations(dateTime1)) {

            if (dateTime1.plusDays(reservationLimit).isBefore(dateTime2)) {
                dateTime2 = dateTime1.plusDays(reservationLimit);
            }

            List<LocalDateTime> availableDates = findAvailability(dateTime1.toLocalDate(),
                    dateTime2.toLocalDate());

            if (availableDates.containsAll(new DateRange(dateTime1, dateTime2).toList())) {
                Reservation reservation = bookingComponent.recordBooking(
                        new Reservation(dateTime1, dateTime2, user));

                log.info(String.format("%s %s - %s", "Reservation created for dates:",
                        dateTime1.toString(), dateTime2.toString()));

                return reservation.getReservationId();
            }

            String errMsg = String.format("%s %s %s", "Invalid reservation values:",
                    dateTime1.toString(), dateTime2.toString());

            log.info(errMsg);

            return null;
        }

        log.info(String.format("%s %s", "Advanced booking constrains violated for date:",
                startDate.toString()));

        return null;
    }


    public void remove(Long id) {
        bookingComponent.removeBooking(id);
    }

    public Boolean update(Long id, LocalDate startDate, LocalDate endDate) {
        Optional<Reservation> optionalReservation = bookingComponent.findReservation(id);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setReservationId(id);
            reservation.setStartDate(startDate.atTime(12, 00));
            reservation.setEndDate(endDate.atTime(12, 00));

            bookingComponent.recordBooking(reservation);
            return true;
        }
        return false;
    }

}

