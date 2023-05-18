package com.example.todo_app.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.todo_app.dao.TodoDao;
import com.example.todo_app.models.Todo;

@Database(entities = Todo.class, version = 1, exportSchema = false)
public abstract class TodoDatabase extends RoomDatabase {

    private static TodoDatabase todoDatabase;

    public static synchronized TodoDatabase getTodoDatabase(Context context) {
        if (todoDatabase == null) {
            todoDatabase = Room.databaseBuilder( context, TodoDatabase.class, "todo_db"
                                                ).build();
        }
        return todoDatabase;
    }

    public abstract TodoDao todoDao();
}
