package com.appyware.medicube;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Reports")
public class TaskDemo extends ParseObject {
    public TaskDemo() {

    }

    public boolean isCompleted() {
        return getBoolean("completed");
    }

    public void setCompleted(boolean complete) {
        put("completed", complete);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public ParseFile getImage() {

        return (ParseFile) get("file");
    }

    public void setImage(ParseFile image) {
        put("file", image);
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public void setUser(ParseUser currentUser) {
        put("user", currentUser);
    }
}
