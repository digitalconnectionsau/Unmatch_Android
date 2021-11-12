package com.contact.unmatch.model;

import android.content.Context;
import android.text.TextUtils;

import com.contact.unmatch.utils.Utils;
import com.google.gson.Gson;

public class User {

    private String access_token;
    private String refresh_token;
    private String email;
    private String password;

    private static User instance;

    public void setAccessToken(String token) {
        access_token = token;
    }

    public String getAccessToken(){
        return access_token;
    }

    public void setRefreshToken(String token) {
        refresh_token = token;
    }

    public String getRefreshToken(){
        return refresh_token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static User getInstance() {
        if(instance == null){
            instance = new User();
        }
        return instance;
    }

    public static User getInstance(boolean isNew) {
        if(isNew){
            instance = new User();
        } else {
            if(instance == null){
                instance = new User();
            }
        }
        return instance;
    }

    public static User getUser() {
        if (instance == null) {
            Gson gson = new Gson();
            String userJson = Utils.loadPreferences("CURRENT_USER");
            if (!TextUtils.isEmpty(userJson))
                instance = gson.fromJson(userJson, User.class);
        }
        return instance;
    }

    public static void saveUser(User user) {
        instance = user;
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        Utils.savePreferences("CURRENT_USER", userJson);
    }

    public static void deleteUser() {
        Utils.deletePreferences("CURRENT_USER");
        instance = null;
    }
}
