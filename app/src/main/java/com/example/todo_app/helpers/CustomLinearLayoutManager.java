package com.example.todo_app.helpers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomLinearLayoutManager extends LinearLayoutManager {

    private int extraSpaceStart;
    private int extraSpaceEnd;

    // Constructor
    public CustomLinearLayoutManager(Context context, int extraSpaceStart, int extraSpaceEnd) {
        super(context);
        this.extraSpaceStart = extraSpaceStart;
        this.extraSpaceEnd = extraSpaceEnd;
    }

    @Override
    protected void calculateExtraLayoutSpace(@NonNull RecyclerView.State state, @NonNull int[] extraLayoutSpace) {
        extraLayoutSpace[0] = extraSpaceStart;
        extraLayoutSpace[1] = extraSpaceEnd;
    }
}