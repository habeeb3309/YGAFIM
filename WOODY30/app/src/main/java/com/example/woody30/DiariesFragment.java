package com.example.woody30;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DiariesFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Diaries");

    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    NoteAdapter noteAdapter;

    public DiariesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_diaries, container, false);

        addNoteBtn = view.findViewById(R.id.add_note_btn);
        recyclerView = view.findViewById(R.id.recyler_view);
        menuBtn = view.findViewById(R.id.menu_btn);

        addNoteBtn.setOnClickListener((v)-> startActivity(new Intent(requireContext(),NoteDetailsActivity.class)) );
        setupRecyclerView();

        return view;
    }

    void setupRecyclerView(){
        // Query to get all notes from Firestore, sorted by timestamp in descending order
        Query query  = Utility.getCollectionReferenceForDiaries().orderBy("timestamp",Query.Direction.DESCENDING);

        // Set up for FirestoreRecyclerAdapter
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();

        // Set up RecyclerView with LinearLayoutManager and NoteAdapter
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        noteAdapter = new NoteAdapter(options,requireContext());
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    public void onStart() {
        // Start listening for updates when the fragment is started
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    public void onStop() {
        // Stop listening for updates when the fragment is stopped
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    public void onResume() {
        // Notify the adapter of data changes when the fragment is resumed
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}