package com.cuit.hyh.maskguide.model;

import android.view.View;

public class HighlightView {
    private View view;
    private String description;

    public HighlightView(View view, String description) {
        this.view = view;
        this.description = description;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
