package com.upgrade.upgradejavachallenge.controllers;

import com.upgrade.upgradejavachallenge.dto.AddRequestDTO;
import com.upgrade.upgradejavachallenge.model.User;
import com.upgrade.upgradejavachallenge.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/campsite")
public class CampsiteController {
    private ReservationService reservationService;

    @Autowired
    public CampsiteController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/availabilty")
    public List<Date> getAvailability(Date startDate, Date endDate) {
        return reservationService.getAvailability(startDate, endDate);
    }

    @PostMapping("/reserve")
    public ResponseEntity makeReservation(@RequestBody AddRequestDTO addRequestDTO) {

        User user = new User(addRequestDTO.getFullName(), addRequestDTO.getEmail());

        Optional<Long> optionalReservationId = reservationService.
                reserve(addRequestDTO.getDateRange(), user);

        if (optionalReservationId.isPresent())
            return new ResponseEntity(optionalReservationId.get(), HttpStatus.CREATED);
        else
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

    }

}
