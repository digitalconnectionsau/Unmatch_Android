package com.contact.unmatch.model;

import java.io.Serializable;

public class MessageCategory implements Serializable {

    private String category;
    private int messageId;
    private String message;

    public MessageCategory(String m_cateogry, int m_messageId, String m_message){
        category = m_cateogry;
        messageId = m_messageId;
        message = m_message;
    }

    public void setCategory(String m_cateogry){
        category = m_cateogry;
    }
    public String getCategory(){
        return category;
    }
    public void setMessageId(int m_messageId){
        messageId = m_messageId;
    }
    public int getMessageId(){
        return messageId;
    }
    public void setMessage(String m_message){
        message = m_message;
    }
    public String getMessage(){
        return message;
    }
}
