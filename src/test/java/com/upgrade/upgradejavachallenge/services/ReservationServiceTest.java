package com.upgrade.upgradejavachallenge.services;

import com.upgrade.upgradejavachallenge.component.BookingComponent;
import com.upgrade.upgradejavachallenge.model.Reservation;
import com.upgrade.upgradejavachallenge.model.User;
import com.upgrade.upgradejavachallenge.util.DateRange;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@RunWith(SpringJUnit4ClassRunner.class)
class ReservationServiceTest {
    private BookingComponent mockBookingComponent
            = Mockito.mock(BookingComponent.class);

    private ReservationService serviceUnderTest;

    @BeforeEach
    public void setUp() {
        serviceUnderTest = new ReservationService(mockBookingComponent);
        ReflectionTestUtils.setField(serviceUnderTest, "bookingMaxLimit", 1);
        ReflectionTestUtils.setField(serviceUnderTest, "bookingMinLimit", 1);
        ReflectionTestUtils.setField(serviceUnderTest, "reservationLimit", 3);
    }

    @Test
    public void whenFindAvailabilityIsInvokedWithProperDatesResultDoesNotIncludeReservedDates() {
        LocalDate startDate = LocalDate.of(2019, 12, 01);
        LocalDate endDate = LocalDate.of(2019, 12, 15);

        LocalDateTime localDateTime1 = LocalDateTime.of(LocalDate.of(2019, 12, 10),
                LocalTime.of(12, 00, 00));
        LocalDateTime localDateTime2 = LocalDateTime.of(LocalDate.of(2019, 12, 13),
                LocalTime.of(12, 00, 00));
        User user = new User("dummy", "dummy@fakeemail.com");

        List<Reservation> dummyReservations = Arrays.asList(
                new Reservation(localDateTime1, localDateTime2, user));

        List<LocalDateTime> expectedAvailabilityValues = new DateRange(startDate.atTime(12, 00),
                endDate.atTime(12, 00)).toList();

        expectedAvailabilityValues.removeAll(new DateRange(localDateTime1, localDateTime2).toList());
        when(mockBookingComponent.getAllReservations()).thenReturn(dummyReservations);


        List<LocalDateTime> actualAvailabilityValues = serviceUnderTest.findAvailability(startDate, endDate);


        verify(mockBookingComponent, times(1)).getAllReservations();
        assertTrue(actualAvailabilityValues.equals(expectedAvailabilityValues));

    }

    @Test
    public void whenFindAvailabilityIsInvokedWithNullValuesThenExceptionIsThrown() {
        assertThrows(NullPointerException.class,
                () -> {
                    serviceUnderTest.findAvailability(null, null);
                });
    }

    @Test
    public void whenFindAvailabilityIsInvokedWithMoreThanOneMonthDateRangeThenAvailabilityDatesAreInAMonthRange() {
        LocalDate startDate = LocalDate.of(2019, 12, 01);
        LocalDate endDate = LocalDate.of(2020, 01, 30);

        when(mockBookingComponent.getAllReservations()).thenReturn(new ArrayList<>());

        List<LocalDateTime> expectedAvailabilityValues = new DateRange(startDate.atTime(12, 00),
                LocalDate.of(2020, 01, 01).atTime(12, 00)).toList();


        List<LocalDateTime> result = serviceUnderTest.findAvailability(startDate, endDate);


        verify(mockBookingComponent, times(1)).getAllReservations();
        Assert.assertTrue(expectedAvailabilityValues.equals(result));
    }

    @Test
    public void whenUpdateBookingIsCalledWithProperDatesThenCorrectReservationIsUpdated() {
        LocalDateTime startDate = LocalDate.of(2019, 12, 01).atTime(12, 00);
        LocalDateTime endDate = LocalDate.of(2020, 12, 05).atTime(12, 00);
        Reservation dummyReservation = new Reservation(startDate,
                endDate, new User("John Smith", "John.Smith@email.com"));
        dummyReservation.setReservationId(1L);
        when(mockBookingComponent.findReservation(1L)).thenReturn(java.util.Optional.of(dummyReservation));
        when(mockBookingComponent.recordBooking(dummyReservation)).thenReturn(dummyReservation);


        Boolean result = serviceUnderTest.update(1L, startDate.toLocalDate(), endDate.toLocalDate());


        verify(mockBookingComponent, times(1))
                .recordBooking(dummyReservation);
        Assert.assertTrue(result);
    }

    @Test
    public void whenUpdateBookingIsCalledWithInvalidDataThenFalseIsReturned() {
        LocalDateTime startDate = LocalDate.of(2019, 12, 01).atTime(12, 00);
        LocalDateTime endDate = LocalDate.of(2020, 12, 05).atTime(12, 00);
        when(mockBookingComponent.findReservation(1L)).thenReturn(null);

        Boolean result = serviceUnderTest.update(100L, startDate.toLocalDate(), endDate.toLocalDate());

        assertFalse(result);
    }

    @Test
    public void whenPerformBookingIsCalledWithNullValuesThenExceptionIsThrown() {
        assertThrows(NullPointerException.class,
                () -> {
                    serviceUnderTest.reserve(null, null, null);
                });
    }
}