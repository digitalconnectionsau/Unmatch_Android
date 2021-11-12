package com.contact.unmatch.model;

import java.io.Serializable;

public class Message implements Serializable {

    private String message;
    private String dateUTC;

    public Message(String m_message, String m_dateUTC) {
        message = m_message;
        dateUTC = m_dateUTC;
    }

    public void setMessage(String m_message) {
        message = m_message;
    }
    public String getMessage(){
        return message;
    }
    public void setDateUTC(String m_dateUTC) {
        dateUTC = m_dateUTC;
    }
    public String getDateUTC() {
        return dateUTC;
    }
}
