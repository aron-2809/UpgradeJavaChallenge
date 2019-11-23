package com.upgrade.upgradejavachallenge.component;

import com.upgrade.upgradejavachallenge.model.Reservation;
import com.upgrade.upgradejavachallenge.model.User;
import com.upgrade.upgradejavachallenge.repository.ReservationRepository;
import com.upgrade.upgradejavachallenge.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
class BookingComponentTest {

    private ReservationRepository mockReservationRepository
            = Mockito.mock(ReservationRepository.class);
    private UserRepository mockUserRepository
            = Mockito.mock(UserRepository.class);

    private BookingComponent classUnderTest;

    @BeforeEach
    public void setUp() {
        classUnderTest = new BookingComponent(mockReservationRepository, mockUserRepository);
        ReflectionTestUtils.setField(classUnderTest, "bookingMaxLimit", 1);
        ReflectionTestUtils.setField(classUnderTest, "bookingMinLimit", 1);
        ReflectionTestUtils.setField(classUnderTest, "reservationLimit", 3);
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
        when(mockReservationRepository.findAll()).thenReturn(dummyReservations);


        List<LocalDateTime> actualAvailabilityValues = classUnderTest.findAvailability(startDate, endDate);


        assertTrue(actualAvailabilityValues.equals(expectedAvailabilityValues));

    }

    @Test
    public void whenFindAvailabilityIsInvokedWithNullValuesThenExceptionIsThrown() {
        assertThrows(NullPointerException.class,
                () -> {
                    classUnderTest.findAvailability(null, null);
                });

    }

    @Test
    public void whenFindAvailabilityIsInvokedWithMoreThanOneMonthDateRangeThenAvailabilityDatesAreInAMonthRange() {
        LocalDate startDate = LocalDate.of(2019, 12, 01);
        LocalDate endDate = LocalDate.of(2020, 01, 30);

        when(mockReservationRepository.findAll()).thenReturn(new ArrayList<>());

        List<LocalDateTime> expectedAvailabilityValues = new DateRange(startDate.atTime(12, 00),
                LocalDate.of(2020, 01, 01).atTime(12, 00)).toList();
        

        List<LocalDateTime> result = classUnderTest.findAvailability(startDate, endDate);


        Assert.assertTrue(expectedAvailabilityValues.equals(result));
    }

}