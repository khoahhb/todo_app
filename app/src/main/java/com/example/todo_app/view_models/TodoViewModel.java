package com.example.todo_app.view_models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todo_app.dao.TodoDao;
import com.example.todo_app.database.TodoDatabase;
import com.example.todo_app.models.Todo;
import com.example.todo_app.repositories.TodoRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;

public class TodoViewModel extends AndroidViewModel {

    private final TodoRepository mRepository;

    private LiveData<List<Todo>> mTodos;

    public MutableLiveData<List<Todo>> checkedList = new MutableLiveData<>();

    public MutableLiveData<String> keyTranfer = new MutableLiveData<>();

    public TodoViewModel(Application application) {
        super(application);
        mRepository = new TodoRepository(application);
        keyTranfer.postValue("");
        checkedList.postValue(new ArrayList<>());
    }

    public MutableLiveData<String> getKeyTranfer() {
        return keyTranfer;
    }

    public MutableLiveData<List<Todo>> getChekedList() {
        return checkedList;
    }

    public LiveData<List<Todo>> getListTodoAll(String keyword) {
        mTodos = mRepository.getListTodoAll(keyword);
        return mTodos;
    }

    public LiveData<List<Todo>> getListTodoByStatus(String status) {
        mTodos = mRepository.getListTodoByStatus(status, keyTranfer.getValue());
        return mTodos;
    }

    public Completable insertTodo(Todo todo) {
        return mRepository.insertTodo(todo);
    }

    public Completable updateTodo(Todo todo) {
        return mRepository.updateTodo(todo);
    }

    public Completable deleteTodo(Todo todo) {
        return mRepository.deleteTodo(todo);
    }

    public Completable deleteSelectedTodos() {
        List<Integer> ids = new ArrayList<>();
        for (Todo e : checkedList.getValue()) {
            ids.add(e.getId());
        }
        return mRepository.deleteSelectedTodos(ids);
    }

    public void resetCheckedList() {
        if (checkedList.getValue() != null) {
            List<Todo> list = new ArrayList<>();
            checkedList.postValue(list);
        }
    }
}
