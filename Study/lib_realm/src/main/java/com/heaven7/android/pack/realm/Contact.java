package com.heaven7.android.pack.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Contact extends RealmObject {
    public String name;
    public RealmList<Email> emails;
}

