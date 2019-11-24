package com.upgrade.upgradejavachallenge;

import com.upgrade.upgradejavachallenge.controllers.CampsiteController;
import com.upgrade.upgradejavachallenge.model.Reservation;
import com.upgrade.upgradejavachallenge.model.User;
import com.upgrade.upgradejavachallenge.services.ReservationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CampsiteController.class)
@ContextConfiguration(classes = {UpgradeJavaChallengeApplication.class})
public class CampsiteIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService mockReservationService;

    @Test
    public void getAvailabilityTest() {
        try {
            mockMvc.perform(get("/campsite/availability")
                    .param("startDate", LocalDate.of(2019, 12, 01).toString())
                    .param("endDate", LocalDate.of(2019, 12, 15).toString()))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getReservationTest() {
        Reservation reservation = new Reservation(
                LocalDate.of(2019, 11, 29).atTime(12, 00),
                LocalDate.of(2019, 11, 30).atTime(12, 00),
                new User("John", "myemail@gmail.com"));
        reservation.setReservationId(1L);

        Optional<Reservation> expectedReservation = Optional.of(reservation);

        when(mockReservationService.find(1L)).thenReturn(expectedReservation);

        try {
            mockMvc.perform(get("/campsite/find")
                    .param("id", new Long(1).toString()))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void reserveTestPositiveScenario() {
        String requestBodyJson = "{\"name\":\"user\", \"email\":\"daf@gmail.com\", \"arrivalDate\":\"2019-12-01\", \"departureDate\":\"2019-12-15\"}";

        LocalDate startDate = LocalDate.of(2019, 12, 01);
        LocalDate endDate = LocalDate.of(2019, 12, 15);

        when(mockReservationService.reserve(startDate, endDate, "user", "daf@gmail.com"))
                .thenReturn(20L);

        try {
            mockMvc.perform(post("/campsite/reserve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void reserveTestNegativeScenatio(){
        try {
            mockMvc.perform(post("/campsite/reserve")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void updateBookingTestPositiveScenario() {
        LocalDate startDate = LocalDate.of(2019, 12, 01);
        LocalDate endDate = LocalDate.of(2019, 12, 15);

        when(mockReservationService.update(1L, startDate, endDate)).thenReturn(true);

        try {
            mockMvc.perform(put("/campsite/modify")
                    .param("id", new Long(1L).toString())
                    .param("arrivalDate", startDate.toString())
                    .param("departureDate", endDate.toString()))
                    .andExpect(status().isNoContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateBookingTestNegativeScenario() {
        LocalDate startDate = LocalDate.of(2019, 12, 12);
        LocalDate endDate = LocalDate.of(2019, 12, 15);

        when(mockReservationService.update(100L, startDate, endDate)).thenReturn(false);

        try {
            mockMvc.perform(put("/campsite/modify")
                    .param("id", new Long(1L).toString())
                    .param("arrivalDate", startDate.toString())
                    .param("departureDate", endDate.toString()))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeBookingTestWithCorrectValues() {

        doNothing().when(mockReservationService).remove(1L);

        try {
            mockMvc.perform(delete("/campsite/remove")
                    .param("id", new Long(1L).toString()))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeBookingTestWithNullValues() {
        try {
            mockMvc.perform(delete("/campsite/remove")
                    .param("id", null))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
