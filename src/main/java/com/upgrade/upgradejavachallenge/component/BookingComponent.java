package com.upgrade.upgradejavachallenge.component;

import com.upgrade.upgradejavachallenge.model.Reservation;
import com.upgrade.upgradejavachallenge.model.User;
import com.upgrade.upgradejavachallenge.repository.ReservationRepository;
import com.upgrade.upgradejavachallenge.repository.UserRepository;
import com.upgrade.upgradejavachallenge.util.DateRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BookingComponent {
    private ReservationRepository reservationRepository;
    private UserRepository userRepository;
    private List<Reservation> reservationList;
    @Value("${campsite.advanced-booking.max-limit-months}")
    private Integer bookingMaxLimit;
    @Value("${campsite.advanced-booking.min-limit-days}")
    private Integer bookingMinLimit;
    @Value("${campsite.booking.reservation-limit-days}")
    private Integer reservationLimit;

    @Autowired
    public BookingComponent(ReservationRepository reservationRepository,
                            UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    public Long performBooking(LocalDate fromDate, LocalDate toDate, User user) {
        LocalDateTime dateTime1 = fromDate.atTime(12, 00);
        LocalDateTime dateTime2 = toDate.atTime(12, 00);

        List<User> existingUsers = userRepository.findAll();

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

            List<LocalDateTime> availableDates = findAvailability(dateTime1.toLocalDate(), dateTime2.toLocalDate());

            if (availableDates.containsAll(new DateRange(dateTime1, dateTime2).toList())) {
                Reservation reservation = reservationRepository.save(
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
                fromDate.toString()));

        return null;
    }

    private Boolean advancedBookingValidations(LocalDateTime date1) {
        return date1.minusMonths(bookingMaxLimit).isBefore(LocalDateTime.now()) &&
                date1.minusDays(bookingMinLimit).isAfter(LocalDateTime.now());
    }

    public List<LocalDateTime> findAvailability(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime dateTime1 = fromDate.atTime(12, 00);
        LocalDateTime dateTime2 = toDate.atTime(12, 00);

        dateTime2 = dateTime1.plusMonths(bookingMaxLimit).isBefore(dateTime2) ?
                dateTime1.plusMonths(bookingMaxLimit) : dateTime2;

        reservationList = reservationRepository.findAll();

        List<LocalDateTime> requestedTravelDates =
                new DateRange(dateTime1, dateTime2).toList();

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

    public void removeBooking(Long id) {
        reservationRepository.deleteById(id);
    }

    public Boolean updateBooking(Long id, LocalDate startDate, LocalDate endDate) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setReservationId(id);
            reservation.setStartDate(startDate.atTime(12, 00));
            reservation.setEndDate(endDate.atTime(12, 00));

            reservationRepository.save(reservation);
            return true;
        }
        return false;
    }
}
