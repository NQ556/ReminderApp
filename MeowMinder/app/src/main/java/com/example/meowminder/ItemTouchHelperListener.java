package com.example.meowminder;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder viewHolder);
}
