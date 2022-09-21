package com.example.remwords.ui.adapter;

public class GroupInfo {
    public final String title;
    public final String message;
    private boolean selected;

    public GroupInfo(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
