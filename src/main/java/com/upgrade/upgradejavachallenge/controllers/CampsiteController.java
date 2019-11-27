package com.upgrade.upgradejavachallenge.controllers;

import com.upgrade.upgradejavachallenge.dto.AddRequestDTO;
import com.upgrade.upgradejavachallenge.exceptions.InvalidInputException;
import com.upgrade.upgradejavachallenge.exceptions.RecordNotFoundException;
import com.upgrade.upgradejavachallenge.model.Reservation;
import com.upgrade.upgradejavachallenge.services.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/campsite")
public class CampsiteController {
    private ReservationService reservationService;

    @Autowired
    public CampsiteController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/availability")
    public ResponseEntity findAvalability(@RequestParam LocalDate startDate,
                                          @RequestParam LocalDate endDate) {
        List<LocalDateTime> availableDates = reservationService.findAvailability(startDate, endDate);

        log.info("Available dates: " + availableDates);

        return new ResponseEntity(availableDates, HttpStatus.OK);
    }

    @GetMapping("/find")
    public ResponseEntity checkReservation(@RequestParam Long id) {
        if (id != null) {
            Optional<Reservation> optionalReservation = reservationService.find(id);

            if (optionalReservation.isPresent()) {
                return new ResponseEntity(optionalReservation.get(), HttpStatus.OK);
            }

            throw new RecordNotFoundException("Invalid reservation id" + id);
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/reserve")
    public ResponseEntity makeReservation(@RequestBody AddRequestDTO addRequestDTO) {
        if (addRequestDTO.getName() != null && addRequestDTO.getEmail() != null
                && addRequestDTO.getArrivalDate() != null
                && addRequestDTO.getDepartureDate() != null) {

            Optional<Long> optionalReservationId = Optional.ofNullable(reservationService
                    .reserve(addRequestDTO.getArrivalDate(), addRequestDTO.getDepartureDate(),
                            addRequestDTO.getName(), addRequestDTO.getEmail()));

            if (optionalReservationId.isPresent()) {
                Long reservationId = optionalReservationId.get();

                log.info(String.format("%s %d", "Reservation created with booking id:", reservationId));

                return new ResponseEntity(reservationId, HttpStatus.CREATED);
            } else {
                throw new InvalidInputException("Invalid input values.");
            }
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/remove")
    public ResponseEntity removeReservation(@RequestParam Long id) {
        if (id != null) {
            reservationService.remove(id);

            log.info(String.format("%s %d %s", "Reservation with id", id, "removed."));

            return new ResponseEntity(id, HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/modify")
    public ResponseEntity modifyReservation(@RequestParam Long id,
                                            @RequestParam LocalDate startDate,
                                            @RequestParam LocalDate endDate) {

        if (id != null && startDate != null && endDate != null
                && startDate.isBefore(endDate)) {
            if (reservationService.update(id, startDate, endDate)) {
                log.info(String.format("%s %d %s", "Reservation with id", id, "modified."));

                return new ResponseEntity(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
