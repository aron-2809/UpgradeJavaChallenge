package com.upgrade.upgradejavachallenge.dto;

import com.upgrade.upgradejavachallenge.util.DateRange;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddRequestDTO {
    private String fullName;
    private String email;
    private DateRange dateRange;
}
