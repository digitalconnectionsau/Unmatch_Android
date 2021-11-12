package com.contact.unmatch.model;

import java.io.Serializable;

public class Exes implements Serializable {

    private int id;
    private String name;
    private String phoneNumber;
    private int breakupMessageId;

    public Exes(int m_id, String m_name, String m_phoneNumber, int m_breakupMessageId){
        id = m_id;
        name = m_name;
        phoneNumber = m_phoneNumber;
        breakupMessageId = m_breakupMessageId;
    }
    public void setId(int m_id){
        id = m_id;
    }
    public int getId() {
        return id;
    }
    public void setName(String m_name){
        name = m_name;
    }
    public String getName(){
        return name;
    }
    public void setPhoneNumber(String m_phoneNumber){
        phoneNumber = m_phoneNumber;
    }
    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setBreakupMessageId(int m_breakupMessageId){
        breakupMessageId = m_breakupMessageId;
    }
    public int getBreakupMessageId(){
        return breakupMessageId;
    }
}
