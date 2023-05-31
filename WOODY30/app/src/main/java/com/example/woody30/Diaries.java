package com.example.woody30;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.common.collect.Maps;


public class Diaries extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton addDiaryBtn;
   // FloatingActionButton chatBtn;

   private static final String REDIRECT_URI = "yourcustomprotocol://callback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diaries);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //  if (savedInstanceState == null) {
      //      getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
      //              new DiariesFragment()).commit();
      //  }

        //addDiaryBtn = findViewById(R.id.addDiaryBtn);
        //switching fragment
        //addDiaryBtn.setOnClickListener((v)-> startActivity(new Intent(Diaries.this, DiaryDetails.class)));

        //chatBtn = findViewById(R.id.ChatBtn);
        //chatBtn.setOnClickListener((v)-> startActivity(new Intent(Diaries.this, ChatBot.class)));
        //chatBtn.setOnClickListener(new View.OnClickListener() {
          //  @Override
            //public void onClick(View view) {
              //  openChatbot();
            //}
        //});
    }
   public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case  R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DiariesFragment()).commit();
                break;
           case  R.id.nav_diary:
               getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                       new DiariesFragment()).commit();
               break;
            case  R.id.nav_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MapFragment()).commit();
                break;
            case  R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatbotFragment()).commit();
                break;
            case R.id.nav_email:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new EmailFragment()).commit();
                Toast.makeText(this, "Email", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

   // public void openChatbot(){
     //   Intent intent = new Intent(this,ChatBot.class);
    //}
}