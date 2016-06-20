package com.kedarv.pipette;

/**
 * Created by kedar on 6/15/16.
 */
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private List<Message> messageList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView sender, text;
        public LinearLayout background, content;
        public MyViewHolder(View view) {
            super(view);
            sender = (TextView) view.findViewById(R.id.sender);
            text = (TextView) view.findViewById(R.id.text);
            background = (LinearLayout) view.findViewById(R.id.contentWithBackground);
            content = (LinearLayout) view.findViewById(R.id.content);
        }
    }


    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
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

        if(message.getFrom_me() == 1) {
            if(message.getStatus() == 0) {
                holder.background.setBackgroundResource(R.drawable.chat_unsent);
            }
            else {
                holder.background.setBackgroundResource(R.drawable.chat_me);
            }
            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.background.getLayoutParams();
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
            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.background.getLayoutParams();
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
