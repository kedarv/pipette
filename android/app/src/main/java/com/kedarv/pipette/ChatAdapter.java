package com.kedarv.pipette;

/**
 * Created by kedar on 6/15/16.
 */
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<Chat> chatList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView people, date;
        public ImageView image_view;

        public MyViewHolder(View view) {
            super(view);
            people = (TextView) view.findViewById(R.id.people);
            date = (TextView) view.findViewById(R.id.date);
            image_view = (ImageView) view.findViewById(R.id.image_view);
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
        holder.people.setText(chat.getFormattedDate());
        holder.date.setText(chat.getPeopleAsString());
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                    .width(100)
                    .height(100)
                    .fontSize(45)
                .endConfig()
                .buildRound(chat.getBubbleText(), chat.getColor());
        holder.image_view.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
