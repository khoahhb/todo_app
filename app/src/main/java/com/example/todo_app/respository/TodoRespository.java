package com.example.todo_app.respository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.todo_app.dao.TodoDao;
import com.example.todo_app.database.TodoDatabase;
import com.example.todo_app.models.Todo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class TodoRespository {

    private final TodoDatabase todoDatabase;
    private Flowable<List<Todo>> mTodos;

    public TodoRespository(Application application) {
        todoDatabase  = TodoDatabase.getTodoDatabase(application);
    }

    public Flowable<List<Todo>> getListTodoFiltering(String keyword) {
        mTodos = todoDatabase.todoDao().getListTodoFiltering(keyword);
        return mTodos;
    }

    public Flowable<List<Todo>> getListTodoByStatus(String status) {
        mTodos = todoDatabase.todoDao().getListTodoByStatus(status);
        return mTodos;
    }

    public Flowable<List<Todo>> getListTodoByTitle(String tile) {
        mTodos = todoDatabase.todoDao().getListTodoByTile(tile);
        return mTodos;
    }

    public Completable insert(Todo todo) {
       return  todoDatabase.todoDao().insertTodo(todo);
    }

    public Completable update(Todo todo) {
        return todoDatabase.todoDao().updateTodo(todo);
    }

    public Completable delete(Todo todo) {
        return todoDatabase.todoDao().deleteTodo(todo);
    }

    public Completable deleteSelectedTodos(List<Integer> id) {
        return todoDatabase.todoDao().deleteSelectedTodos(id);
    }
}
