package com.example.roomdatabasewithhandler;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NameDao {
    @Insert
    void insert(Name... names);

    @Delete
    int delete(Name... names);

    @Query("DELETE FROM table_name WHERE name = :name")
    int deleteName(String name);

    @Query("SELECT name FROM table_name WHERE name = :name")
    List<String> getName(String name);

    @Query("SELECT name FROM table_name")
    List<String> getAllName();
}
