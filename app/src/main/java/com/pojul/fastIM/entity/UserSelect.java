package com.pojul.fastIM.entity;

public class UserSelect {

    private User user;
    private boolean isSelected;

    public UserSelect() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
