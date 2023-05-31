package com.example.woody30;

// Import libraries
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    // List object messageList to store messages
    List<Message> messageList;

    // Constructor to initialize messageList
    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,null);

        MyViewHolder myViewHolder = new MyViewHolder(chatView);

        // Return the created MyViewHolder object
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        // Get the message object from messageList
        Message message = messageList.get(position);

        // Check if the message is sent by the user
        if(message.getSentBy().equals(Message.SENT_BY_ME)){

            // Hide the left chat view and show the right chat view
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);

            // Set the message text in the right text view
            holder.rightTextView.setText(message.getMessage());

        }else{

            // Hide the right chat view and show the left chat view
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);

            // Set the message text in the left text view
            holder.leftTextView.setText(message.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatView,rightChatView;
        TextView leftTextView,rightTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView  = itemView.findViewById(R.id.left_chat_view);
            rightChatView = itemView.findViewById(R.id.right_chat_view);
            leftTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightTextView = itemView.findViewById(R.id.right_chat_text_view);
        }
    }
}
