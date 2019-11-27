package com.upgrade.upgradejavachallenge.component;

import com.upgrade.upgradejavachallenge.model.Reservation;
import com.upgrade.upgradejavachallenge.model.User;
import com.upgrade.upgradejavachallenge.repository.ReservationRepository;
import com.upgrade.upgradejavachallenge.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.upgrade.upgradejavachallenge.util.TestsUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BookingComponentTest {
    private ReservationRepository mockReservationRepository = Mockito.mock(ReservationRepository.class);

    private UserRepository mockUserRepository = Mockito.mock(UserRepository.class);

    private BookingComponent classUnderTest;

    public BookingComponentTest() {
        classUnderTest = new BookingComponent(mockReservationRepository, mockUserRepository);
    }

    @Test
    public void whenGetAllReservationsIsCalledThenAllReservationsAreReturned() {
        classUnderTest.getAllReservations();

        verify(mockReservationRepository, times(1)).findAll();
    }

    @Test
    public void whenRemoveBookingIsCalledThenCorrectRepositoryMehodWasInvoked() {
        classUnderTest.removeBooking(1L);

        verify(mockReservationRepository, times(1))
                .deleteById(1L);
    }

    @Test
    public void whenRecordBookingIsCalledThenCorrectRepositoryMethodWasInvokedWithGivenArguments() {
        final String userName = getUUID();
        final String userEmail = getUUID();
        final User user = new User(userName, userEmail);
        final Reservation expectedReservation = new Reservation(dateTime2, dateTime3, user);

        when(mockUserRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(mockReservationRepository.save(any(Reservation.class))).thenReturn(expectedReservation);


        Reservation actualReservation =
                classUnderTest.recordBooking(dateTime1, dateTime3, userName, userEmail);


        assertThat(actualReservation.equals(expectedReservation));
    }

    @Test
    public void whenRecordBookingIsCalledWithExistingUserThenReservationsAreUpdated() {
        final String userName = getUUID();
        final String userEmail = getUUID();
        final User user = new User(userName, userEmail);

        when(mockUserRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(mockUserRepository.save(user)).thenReturn(user);


        Reservation expectedReservation = classUnderTest.recordBooking(dateTime9, dateTime11, userName, userEmail);


        assertThat(expectedReservation != null);
    }


    @Test
    public void whenRecordBookingIsCalledWithReferenceThenCorrectRepositoryMehtodWasInvokedWithGivenArguments() {
        final String userName = getUUID();
        final String userEmail = getUUID();
        final User user = new User(userName, userName);
        final Reservation reservation = new Reservation(dateTime2, dateTime4, user);


        classUnderTest.recordBooking(reservation);


        verify(mockReservationRepository, times(1))
                .save(reservation);
    }

    @Test
    public void whenFindReservationIsCalledWithReservationIdThenCorrectRepositoryMethodIsInvokedWithGivenArgument() {


        classUnderTest.findReservation(200L);


        verify(mockReservationRepository, times(1))
                .findById(200L);
    }
}