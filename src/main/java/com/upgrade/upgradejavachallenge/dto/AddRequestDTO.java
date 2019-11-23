package com.upgrade.upgradejavachallenge.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AddRequestDTO {
    private String name;
    private String email;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
}
