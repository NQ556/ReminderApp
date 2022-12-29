package com.example.meowminder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> noteList;
    private Context context;

    public NoteAdapter(List<Note> noteList) {
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);
        context = parent.getContext();
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);

        if (note == null)
        {
            return;
        }

        //Set title name
        holder.title.setText(note.getTitle());

        //Set time
        holder.time.setText(note.getTime());


        //Set first task
        holder.firstTask.setText(note.getFirstTask());

        //Set notification
        boolean isAlarmOn = note.getIsAlarmOn();
        if (isAlarmOn)
        {
            holder.notification.setImageResource(R.drawable.notification);
        }

        else
        {
            holder.notification.setImageResource(R.drawable.no_notification);
        }

        //Show recipe's information when click on item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewNote.class);
                intent.putExtra("id", note.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (noteList != null)
        {
            return noteList.size();
        }
        return 0;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView time;
        private TextView firstTask;
        private ImageView notification;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            time = (TextView) itemView.findViewById(R.id.time);
            firstTask = (TextView) itemView.findViewById(R.id.first_task);
            notification = (ImageView) itemView.findViewById(R.id.notification);
        }
    }
}
