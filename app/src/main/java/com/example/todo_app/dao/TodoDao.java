package com.example.todo_app.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todo_app.models.Todo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertTodo(Todo todo);

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :keyword || '%'")
    LiveData<List<Todo>> getListTodoAll(String keyword);

    @Query("SELECT * FROM todos WHERE status=:status AND title LIKE '%' || :keyword || '%'")
    LiveData<List<Todo>> getListTodoByStatus(String status, String keyword);

    @Update
    Completable updateTodo(Todo item);

    @Delete
    Completable deleteTodo(Todo item);

    @Query("delete from todos where id in (:id)")
    Completable deleteSelectedTodos(List<Integer> id);
}
