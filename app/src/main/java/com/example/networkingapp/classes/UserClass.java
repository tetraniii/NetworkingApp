package com.example.networkingapp.classes;

import java.util.ArrayList;
import java.util.List;

public class UserClass {
    private String name;
    private String desc;
    private String id;

    public UserClass(String name, String desc, String id){
        this.name = name;
        this.desc = desc;
        this.id = id;
    }

    public String getUserName() {
        return name;
    }

    public String getUserDesc() {
        return desc;
    }

    public String getUserID() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserClass(){}
}
