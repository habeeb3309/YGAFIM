package com.example.woody30;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatbotFragment extends Fragment {

    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    ChatAdapter messageAdapter;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chatbot, container, false);

        // Initializing the message list
        messageList = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.recycler_view);
        welcomeTextView = rootView.findViewById(R.id.welcome_text);
        messageEditText = rootView.findViewById(R.id.message_edit_text);
        sendButton = rootView.findViewById(R.id.send_btn);

        //setup recycler view
        messageAdapter = new ChatAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener((v)->{
            // Get the message from the edit text and trim whitespace
            String question = messageEditText.getText().toString().trim();

            // Add the message to the chat list "sent by me"
            addToChat(question,Message.SENT_BY_ME);
            messageEditText.setText("");

            // Call the OpenAI API with the user's question
            callAPI(question);

            // Hide the welcome message
            welcomeTextView.setVisibility(View.GONE);
        });

        return rootView;
    }

    // Helper method to add a message to the chat list
    void addToChat(String message,String sentBy){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Add the message to the list and notify the adapter of the change
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();

                // Scroll to the bottom of the list
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response){
        // Remove the "typing" message that was added when the API call was made
        messageList.remove(messageList.size()-1);
        // Add the API response to the chat list "sent by bot"
        addToChat(response,Message.SENT_BY_BOT);
    }

    void callAPI(String question) {

        // Show a "typing" message while waiting for the API response
        messageList.add(new Message("Typing... ", Message.SENT_BY_BOT));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "text-davinci-003");
            jsonBody.put("prompt", question);
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // If the API call is successful, parse the JSON response and add the result to the chat
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    // If the API call fails, add an error message to the chat
                    addResponse("Failed to load response due to " + response.body().toString());
                }
            }
        });
    }
}