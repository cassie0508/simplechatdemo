package com.simplechat.database;

public class user {
    private int id;
    private String name;
    private String password;
    private String phone;
    private int is_online;
    private String friends;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setIs_online(int is_online) {
        this.is_online = is_online;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

}
