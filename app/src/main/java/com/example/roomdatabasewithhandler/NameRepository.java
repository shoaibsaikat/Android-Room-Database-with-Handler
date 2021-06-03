package com.example.roomdatabasewithhandler;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.room.Room;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.example.roomdatabasewithhandler.MainActivity.LOG_TAG;
import static com.example.roomdatabasewithhandler.NameDatabase.DB_NAME;

public class NameRepository {

    public static final int INSERT = 1;
    public static final int GET = 2;

    private final ThreadPoolExecutor executorService;
    private final NameDatabase db;
    private final NameDao dao;
    private final Handler mainHandler;

    public NameRepository(Context context, Handler handler) {
        mainHandler = handler;
        executorService = new ThreadPoolExecutor(4, 5, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        db = Room.databaseBuilder(context, NameDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
        dao = db.nameDao();
    }

    void insert(final Name name) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    dao.insert(name);
                    Message msg = mainHandler.obtainMessage(INSERT, name.getName());
                    msg.sendToTarget();
                } catch (SQLiteConstraintException e) {
                    Log.e(LOG_TAG, e.toString());
                }
            }
        });
    }

    void getAllName() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List names = dao.getAllName();
                Message msg = mainHandler.obtainMessage(GET, names);
                msg.sendToTarget();
            }
        });
    }
}
