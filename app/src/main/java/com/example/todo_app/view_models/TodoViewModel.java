package com.example.todo_app.view_models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todo_app.dao.TodoDao;
import com.example.todo_app.database.TodoDatabase;
import com.example.todo_app.models.Todo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;

public class TodoViewModel extends AndroidViewModel {

    private final TodoDao mTodoDao;

    private LiveData<List<Todo>> mTodos;

    public List<Todo> checkedList;

    public MutableLiveData<String> keyTranfer = new MutableLiveData<>();

    public TodoViewModel(Application application) {
        super(application);
        mTodoDao = TodoDatabase.getTodoDatabase(application).todoDao();
        keyTranfer.postValue("");
        this.checkedList = new ArrayList<>();
    }

    public MutableLiveData<String> getKeyTranfer() {
        return keyTranfer;
    }

    public LiveData<List<Todo>> getListTodoAll() {
        mTodos = mTodoDao.getListTodoAll(keyTranfer.getValue());
        return mTodos;
    }

    public LiveData<List<Todo>> getListTodoByStatus(String status) {
        mTodos = mTodoDao.getListTodoByStatus(status, keyTranfer.getValue());
        return mTodos;
    }

    public Completable insert(Todo todo) {
        return mTodoDao.insertTodo(todo);
    }

    public Completable update(Todo todo) {
        return mTodoDao.updateTodo(todo);
    }

    public Completable delete(Todo todo) {
        return mTodoDao.deleteTodo(todo);
    }

    public Completable deleteSelectedTodos() {
        List<Integer> ids = new ArrayList<>();
        for (Todo e : checkedList) {
            ids.add(e.getId());
        }
        return mTodoDao.deleteSelectedTodos(ids);
    }

    public void resetCheckedList() {
        if (checkedList.size() > 0) {
            checkedList.clear();
        }
    }
}
