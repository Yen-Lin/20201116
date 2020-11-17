package com.koddev.authenticatorapp.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koddev.authenticatorapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageHolder> {
    private final String TAG = "ChatMessageAdapter";
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1;

    private List<ChatMessage> mMessages;
    private Context mContext;

    public ChatMessageAdapter(Context context, List<ChatMessage> data) {
        mContext = context;
        mMessages = data;
    }

    @Override
    public int getItemCount() {
        return mMessages == null ? 0 : mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = mMessages.get(position);

        if (item.isMine())
            return MY_MESSAGE;
        else
            return OTHER_MESSAGE;
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MY_MESSAGE) {
            return new MessageHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false));
        } else {
            return new MessageHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false));
        }
    }

    public void add(ChatMessage message) {
        mMessages.add(message);
        notifyItemInserted(mMessages.size() - 1);
    }

    public List<ChatMessage> getMessage()
    {
        return mMessages;
    }

    @Override
    public void onBindViewHolder(final MessageHolder holder, final int position) {
        ChatMessage chatMessage = mMessages.get(position);
        if (chatMessage.isImage()) {
            holder.item_image.setVisibility(View.VISIBLE);
            holder.show_message.setVisibility(View.GONE);
        } else {
            holder.item_image.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.VISIBLE);

            holder.show_message.setText(chatMessage.getContent());
        }

//        String date = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());

        String date = null;
        try
        {

            //將時間轉為小時分鐘，並顯示下午上午
            Date orderDateStart = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss").parse(chatMessage.dateTime());
            date = new SimpleDateFormat("hh:mm aa").format(orderDateStart);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        if(date !=null)
            holder.uptime.setText(date);

    }

    class MessageHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ImageView item_image;
        public TextView uptime;

        public TextView text_seen;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            item_image = itemView.findViewById(R.id.item_image);
            //text_seen = itemView.findViewById(R.id.text_seen);
            uptime = itemView.findViewById(R.id.uptime);
        }
    }
}
