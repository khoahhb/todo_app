package com.example.todo_app.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.todo_app.database.TodoDatabase;
import com.example.todo_app.models.Todo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class TodoViewModel extends AndroidViewModel {
    private TodoDatabase todoDatabase;
    private Flowable<List<Todo>> mTodos;

    public TodoViewModel(Application application) {
        super(application);
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
