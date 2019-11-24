package com.upgrade.upgradejavachallenge.services;

import com.upgrade.upgradejavachallenge.component.BookingComponent;
import com.upgrade.upgradejavachallenge.model.Reservation;
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
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class ReservationService {
    private BookingComponent bookingComponent;

    @Value("${campsite.advanced-booking.max-limit-months}")
    private Integer bookingMaxLimitMonth;

    @Value("${campsite.advanced-booking.min-limit-days}")
    private Integer bookingMinLimitDays;

    @Value("${campsite.booking.reservation-limit-days}")
    private Integer reservationLimitDays;

    private final BiPredicate<LocalDateTime, LocalDateTime> reservationLimitPredicate
            = (localDate1, localDate2) -> localDate1.plusDays(reservationLimitDays).isAfter(localDate2);

    private final BiPredicate<LocalDateTime, LocalDateTime> maxBookingPredicate
            = (localDate1, localDate2) -> localDate1.plusMonths(bookingMaxLimitMonth).isBefore(localDate2);

    private final Predicate<LocalDateTime> advBookingMaxPredicate
            =  localDateTime1 -> localDateTime1.minusMonths(bookingMaxLimitMonth).isBefore(LocalDateTime.now());

    private final Predicate<LocalDateTime> advBookingMinPredicate
            = localDateTime1 -> localDateTime1.minusDays(bookingMinLimitDays).isAfter(LocalDateTime.now());

    @Autowired
    public ReservationService(BookingComponent bookingComponent) {
        this.bookingComponent = bookingComponent;
    }

    public List<LocalDateTime> findAvailability(LocalDate startDate, LocalDate endDate) {
        LocalDateTime dateTime1 = startDate.atTime(12, 00);
        LocalDateTime dateTime2 = endDate.atTime(12, 00);

        dateTime2 = maxBookingPredicate.test(dateTime1, dateTime2) ?
                dateTime1.plusMonths(bookingMaxLimitMonth) : dateTime2;

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
        return advBookingMaxPredicate.test(date1) && advBookingMinPredicate.test(date1);
    }

    public Optional<Reservation> find(Long id) {
        return bookingComponent.findReservation(id);
    }

    public Long reserve(LocalDate startDate, LocalDate endDate, String name, String email) {
        LocalDateTime dateTime1 = startDate.atTime(12, 00);
        LocalDateTime dateTime2 = endDate.atTime(12, 00);

        if (advancedBookingValidations(dateTime1)) {

            if (!reservationLimitPredicate.test(dateTime1, dateTime2)) {
                dateTime2 = dateTime1.plusDays(reservationLimitDays);
            }

            List<LocalDateTime> availableDates = findAvailability(dateTime1.toLocalDate(),
                    dateTime2.toLocalDate());

            if (availableDates.containsAll(new DateRange(dateTime1, dateTime2).toList())) {
                Reservation reservation = bookingComponent.recordBooking(dateTime1, dateTime2, name, email);

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
            LocalDateTime localDateTime1 = startDate.atTime(12, 00);
            LocalDateTime localDateTime2 = endDate.atTime(12, 00);

            Reservation reservation = optionalReservation.get();
            reservation.setReservationId(id);

            localDateTime2 = reservationLimitPredicate.test(localDateTime1, localDateTime2) ?
                    localDateTime2 : localDateTime1.plusDays(reservationLimitDays);

            reservation.setStartDate(localDateTime1);
            reservation.setEndDate(localDateTime2);

            bookingComponent.recordBooking(reservation);
            return true;
        }
        return false;
    }

}

