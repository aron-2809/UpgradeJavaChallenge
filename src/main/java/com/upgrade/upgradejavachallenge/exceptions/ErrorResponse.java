package com.upgrade.upgradejavachallenge.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private List<String> messages;
}
