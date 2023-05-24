package com.example.todo_app.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.todo_app.dao.TodoDao;
import com.example.todo_app.database.TodoDatabase;
import com.example.todo_app.models.Todo;

import java.util.List;

import io.reactivex.Completable;

public class TodoRepository {

    private final TodoDao mTodoDao;

    private LiveData<List<Todo>> mTodos;

    public TodoRepository(Application application) {
        TodoDatabase db = TodoDatabase.getTodoDatabase(application);
        mTodoDao = db.todoDao();
    }

    public LiveData<List<Todo>> getListTodoAll(String keyword) {
        mTodos = mTodoDao.getListTodoAll(keyword);
        return mTodos;
    }

    public LiveData<List<Todo>> getListTodoByStatus(String status, String keyword) {
        mTodos = mTodoDao.getListTodoByStatus(status, keyword);
        return mTodos;
    }

    public Completable insertTodo(Todo todo) {
        return mTodoDao.insertTodo(todo);
    }

    public Completable updateTodo(Todo todo) {
        return mTodoDao.updateTodo(todo);
    }

    public Completable deleteTodo(Todo todo) {
        return mTodoDao.deleteTodo(todo);
    }

    public Completable deleteCheckedTodos(List<Integer> ids) {
        return mTodoDao.deleteCheckedTodos(ids);
    }

}
