package com.example.woody30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class DiaryDetails extends AppCompatActivity {


    EditText titleEditText,contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode = false;
    TextView deleteNoteTextViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_details);

        titleEditText = findViewById(R.id.diarytitle_text);
        contentEditText = findViewById(R.id.diary_content_text);
        saveNoteBtn = findViewById(R.id.save_diary_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewBtn  = findViewById(R.id.delete_diary_text_view_btn);

        // Receive data from previous activity
        title = getIntent().getStringExtra("title");
        content= getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        // Check if in edit mode
        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        // Set title and content EditTexts
        titleEditText.setText(title);
        contentEditText.setText(content);

        // If in edit mode, update page title and make delete button visible
        if(isEditMode){
            pageTitleTextView.setText("Edit your Diary");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

        //click listener for save button
        saveNoteBtn.setOnClickListener( (v)-> saveNote());

        //click listener for delete button
        deleteNoteTextViewBtn.setOnClickListener((v)-> deleteNoteFromFirebase() );

    }

    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();

        // Validate title
        if(noteTitle==null || noteTitle.isEmpty() ){
            titleEditText.setError("Title is required");
            return;
        }

        // Create new note object
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        // Save note to Firebase
        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;

        if(isEditMode){
            // Update the note if in edit mode
            documentReference = Utility.getCollectionReferenceForDiaries().document(docId);
        }else{
            // Create new note if not in edit mode
            documentReference = Utility.getCollectionReferenceForDiaries().document();
        }

        // Set the note data to the document
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    // Note is added successfully
                    Utility.showToast(DiaryDetails.this,"Diary added successfully");
                    finish();
                }else{
                    // Failed to add note
                    Utility.showToast(DiaryDetails.this,"Failed while adding diary");
                }
            }
        });
    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForDiaries().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(DiaryDetails.this,"Diary deleted successfully");
                    finish();
                }else{
                    Utility.showToast(DiaryDetails.this,"Failed while deleting Diary");
                }
            }
        });
    }


}