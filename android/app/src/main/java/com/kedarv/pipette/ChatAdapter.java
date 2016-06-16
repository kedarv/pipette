package com.kedarv.pipette;

/**
 * Created by kedar on 6/15/16.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<Chat> chatList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView people, date;

        public MyViewHolder(View view) {
            super(view);
            people = (TextView) view.findViewById(R.id.genre);
            date = (TextView) view.findViewById(R.id.year);
        }
    }


    public ChatAdapter(List<Chat> chatList) {
        this.chatList = chatList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.people.setText(chat.getReadableDate());
        holder.date.setText(chat.getPeopleAsString());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
