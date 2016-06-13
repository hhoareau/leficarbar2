package com.ficar.shared;

/**
 * Created by u016272 on 29/03/2016.
 */

public class infoFacebook {
    public String email="";
    String id="";
    String link="";
    String first_name="";
    String last_name="";
    String locale="";

    public infoFacebook(String s) {
        email=s;
        id=s;
        first_name=s.split("@")[0];
        last_name=first_name;
        link=s;
        locale="";
    }


    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public infoFacebook(String email, String id) {
        super();
        this.email = email;
        this.id = id;
    }

    public infoFacebook(){}


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

