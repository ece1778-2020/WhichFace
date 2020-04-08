package com.example.spiral2;

import androidx.annotation.Nullable;

public class Cell {
    @Nullable
    private String mData;

    public Cell(@Nullable String data) {
        this.mData = data;
    }

    @Nullable
    public String getData() {
        return mData;
    }
}