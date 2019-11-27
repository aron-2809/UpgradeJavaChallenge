package com.upgrade.upgradejavachallenge.exceptions;


import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class ErrorResponse {
    private String error;
    private List<String> messages;

    public ErrorResponse(String error, List<String> messages) {
        this.error = error;
        this.messages = messages;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
