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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.upgrade.upgradejavachallenge.util.TestsUtil.*;
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
        ReflectionTestUtils.setField(serviceUnderTest, "bookingMaxLimitMonth", 1);
        ReflectionTestUtils.setField(serviceUnderTest, "bookingMinLimitDays", 1);
        ReflectionTestUtils.setField(serviceUnderTest, "reservationLimitDays", 3);
    }

    @Test
    public void whenFindAvailabilityIsInvokedWithProperDatesResultDoesNotIncludeReservedDates() {
        User user = new User("dummy", "dummy@fakeemail.com");

        List<Reservation> dummyReservations = Arrays.asList(
                new Reservation(dateTime10, dateTime13, user));

        List<LocalDateTime> expectedAvailabilityValues = new DateRange(dateTime1, dateTime15).toList();

        expectedAvailabilityValues.removeAll(new DateRange(dateTime10, dateTime13).toList());
        when(mockBookingComponent.getAllReservations()).thenReturn(dummyReservations);


        List<LocalDateTime> actualAvailabilityValues =
                serviceUnderTest.findAvailability(dateTime1.toLocalDate(), dateTime15.toLocalDate());


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
        when(mockBookingComponent.getAllReservations()).thenReturn(new ArrayList<>());

        List<LocalDateTime> expectedAvailabilityValues = new DateRange(dateTime1,
                newDateTime1).toList();


        List<LocalDateTime> actualAvailabilityValues = serviceUnderTest.findAvailability(dateTime1.toLocalDate(),
                newDateTime2.toLocalDate());


        verify(mockBookingComponent, times(1)).getAllReservations();
        Assert.assertTrue(expectedAvailabilityValues.equals(actualAvailabilityValues));
    }

    @Test
    public void whenUpdateBookingIsCalledWithProperDatesThenCorrectReservationIsUpdated() {
        Reservation dummyReservation = new Reservation(dateTime1,
                dateTime3, new User("John Smith", "John.Smith@email.com"));
        dummyReservation.setReservationId(1L);
        when(mockBookingComponent.findReservation(1L)).thenReturn(java.util.Optional.of(dummyReservation));
        when(mockBookingComponent.recordBooking(dummyReservation)).thenReturn(dummyReservation);


        Boolean result = serviceUnderTest.update(1L, dateTime1.toLocalDate(), dateTime3.toLocalDate());


        verify(mockBookingComponent, times(1))
                .recordBooking(dummyReservation);
        Assert.assertTrue(result);
    }

    @Test
    public void whenUpdateBookingIsCalledWithOutOfRangeDatesThenReservationIsUpdatedBasedOnReservationLimit() {
        final Long reservationId = 77L;
        final String userName = getUUID();
        final String userEmail = getUUID();
        final User usr = new User(userName, userEmail);
        usr.setUserId(7L);
        Reservation updateReservation = new Reservation(dateTime7, newDateTime1, usr);

        when(mockBookingComponent.recordBooking(updateReservation)).thenReturn(updateReservation);
        when(mockBookingComponent.findReservation(reservationId)).thenReturn(Optional.of(updateReservation));

        Boolean result = serviceUnderTest.update(reservationId, dateTime7.toLocalDate(),
                newDateTime1.toLocalDate());


        verify(mockBookingComponent, times(1))
                .recordBooking(updateReservation);
        assertTrue(result);
    }


    @Test
    public void whenUpdateBookingIsCalledWithInvalidDataThenFalseIsReturned() {
        when(mockBookingComponent.findReservation(1L)).thenReturn(null);

        Boolean result = serviceUnderTest.update(100L, dateTime1.toLocalDate(), dateTime5.toLocalDate());

        assertFalse(result);
    }

    @Test
    public void whenPerformBookingIsCalledWithNullValuesThenExceptionIsThrown() {
        assertThrows(NullPointerException.class,
                () -> {
                    serviceUnderTest.reserve(null, null, null, null);
                });
    }

    @Test
    public void whenRecordBookingIsCalledWithCorrectValuesThenRecordIsPersisted() {
        final String userName = "Donald Duck";
        final String userEmail = "donald.duck@gmail.com";

        Reservation dummyReservation = new Reservation(dateTime2, dateTime6, new User(userEmail, userEmail));
        dummyReservation.setReservationId(1L);
        when(mockBookingComponent.getAllReservations()).thenReturn(new ArrayList<>());
        when(mockBookingComponent.recordBooking(dateTime2, dateTime6, userName, userEmail))
                .thenReturn(dummyReservation);

        Long result = serviceUnderTest
                .reserve(dateTime2.toLocalDate(), dateTime6.toLocalDate(), userName, userEmail);

        assertTrue(result.equals(1L));
    }
}