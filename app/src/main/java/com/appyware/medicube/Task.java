package com.appyware.medicube;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Reports")
public class Task extends ParseObject {
    public Task() {

    }

    public String getDescription() {
        return getString("desc");
    }

    public void setDescription(String description) {
        put("desc", description);
    }

    public ParseFile getImage() {
        return getParseFile("file");
    }

    public void setEmail(String email) {
        put("Email", email);
    }

    public String getEmail() {
        return getString("Email");
    }

    public void setImage(ParseFile image) {
        put("file", image);
    }

    public String getName() {
        return getString("doctorName");
    }

    public void setName(String name) {
        put("doctorName", name);
    }

    public String getDate() {

        Date subdate = getCreatedAt();
        Calendar cal = Calendar.getInstance();
        cal.setTime(subdate);
        cal.add(Calendar.HOUR, +1);
        cal.add(Calendar.MINUTE, +30);
        Date newdate = cal.getTime();
        //DateFormat df = new SimpleDateFormat("MMM dd, yyyy, kk:mm", Locale.ENGLISH);
        DateFormat df = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
        String subdateStr = df.format(newdate);
        return subdateStr;
    }

    public void setUser(ParseUser currentUser) {
        put("user", "E-mail");
    }
}
