package com.heaven7.android.pack.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by heaven7 on 2017/2/9.
 */

public class User extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String email;

    public User(){}
    public User(String name) {
        this.name = name;
    }

    public void setName(String john) {
        this.name = john;
    }
    public void setEmail(String s) {
        this.email = s;
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
