package com.kedarv.pipette;

/**
 * Created by kedar on 6/15/16.
 */
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private List<Message> messageList;
    public Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView sender, text;
        public LinearLayout background, content;
        public ImageView thumbnail;
        public MyViewHolder(View view) {
            super(view);
            sender = (TextView) view.findViewById(R.id.sender);
            text = (TextView) view.findViewById(R.id.text);
            background = (LinearLayout) view.findViewById(R.id.contentWithBackground);
            content = (LinearLayout) view.findViewById(R.id.content);
            thumbnail = (ImageView) view.findViewById(R.id.image);
        }
    }


    public MessageAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.sender.setText(message.getSender());
        holder.text.setText(message.getText());
        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams) holder.background.getLayoutParams();

        if(message.hasAttachment()) {
            Picasso.with(this.context).load(SocketApplication.getSocketURL() + "/attachment/" + message.getAttachmentID()).into(holder.thumbnail);
            layoutParams.width = 500;
            layoutParams.height = 500;
        }
        else {
            holder.thumbnail.setImageResource(0);
            layoutParams.width = RecyclerView.LayoutParams.WRAP_CONTENT;
            layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT;
        }
        if(message.getFrom_me() == 1) {

            if (message.getStatus() == 0) {
                holder.background.setBackgroundResource(R.drawable.chat_unsent);
            } else {
                holder.background.setBackgroundResource(R.drawable.chat_me);
            }

            layoutParams.gravity = Gravity.RIGHT;
            holder.background.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.text.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.text.setLayoutParams(layoutParams);
            holder.text.setTextColor(Color.WHITE);
            layoutParams = (LinearLayout.LayoutParams) holder.sender.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.sender.setLayoutParams(layoutParams);
        }
        else {

            holder.background.setBackgroundResource(R.drawable.chat_them);

            layoutParams.gravity = Gravity.LEFT;
            holder.background.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.text.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.text.setLayoutParams(layoutParams);
            holder.text.setTextColor(Color.BLACK);
            layoutParams = (LinearLayout.LayoutParams) holder.sender.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.sender.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
