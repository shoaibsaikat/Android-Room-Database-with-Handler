package com.example.roomdatabasewithhandler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.roomdatabasewithhandler.NameRepository.GET;
import static com.example.roomdatabasewithhandler.NameRepository.INSERT;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG  = "CUSTOM_LOG";

    private RecyclerView recyclerView;
    private NameAdapter adapter;
    private int REQUEST_CODE = 1;

    private NameRepository repo;
    private List<String> names;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(Looper.getMainLooper(), new DbMessageCallback());

        repo = new NameRepository(getApplicationContext(), handler);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        names = new ArrayList<>();

        adapter = new NameAdapter();
        recyclerView.setAdapter(adapter);

        // hide title bar
        getSupportActionBar().hide();
    }

    protected void onResume() {
        super.onResume();
        repo.getAllName();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                final String name = data.getStringExtra(NameActivity.KEY).trim();
                repo.insert(new Name(name));
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onAddClicked(View view) {
        Intent intent = new Intent(this, NameActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private class DbMessageCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == INSERT) {
                String name = (String) message.obj;
                names.add(name);
            } else if (message.what == GET) {
                List<String> nameList = (List<String>) message.obj;
                names = nameList;
            }
            adapter.setNames(names);
            return false;
        }
    }
}