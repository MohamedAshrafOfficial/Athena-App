package emad.athena.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import emad.athena.Model.Chat;
import emad.athena.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    public static final int receiver = 0;
    public static final int sender = 1;

    public int flag = 1;
    public int flagSend;
    private Context mContext;
    private List<Chat> mChat;

    public ChatAdapter(Context mContext, List<Chat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    private static final String TAG = "ChatAdapter";

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == sender) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            flagSend = 1;
            return new ChatAdapter.MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            flagSend = 0;
            return new ChatAdapter.MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.show_message.setText(mChat.get(position).getMessage());
        try {
            if (flagSend == 1) {
                Picasso.get().load(mChat.get(position).getProfileImage()).placeholder(R.drawable.defaultpro).into(holder.userChatImage);
            }
        } catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: EXXCCC " + e.getMessage());
        }
        if (mChat.get(position).getUri() != null) {
            Log.d(TAG, "onBindViewHolder URI : " + mChat.get(position).getUri().toString());
        }
        if (mChat.get(position).getUri() != null) {
            Log.d(TAG, "onBindViewHolder: URI" + mChat.get(position).getUri());
            holder.senderImage.setImageURI(mChat.get(position).getUri());
            holder.senderImage.setVisibility(View.VISIBLE);
            // holder.show_message.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (mChat.get(position).getFlag() == flag) {
            return sender;
        } else {
            return receiver;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ImageView profile_image;
        public de.hdodenhof.circleimageview.CircleImageView userChatImage;
        public ImageView senderImage;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            userChatImage = itemView.findViewById(R.id.userChatImage);
            senderImage = itemView.findViewById(R.id.senderImage);
        }
    }
}

