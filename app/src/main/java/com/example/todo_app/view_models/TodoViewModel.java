package com.example.todo_app.view_models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todo_app.models.Todo;
import com.example.todo_app.repositories.TodoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Completable;

public class TodoViewModel extends AndroidViewModel {

    private final TodoRepository mRepository;

    private LiveData<List<Todo>> mTodos;

    public static final MutableLiveData<List<Todo>> checkedList = new MutableLiveData<>();
    public static final MutableLiveData<List<Todo>> uncheckedList = new MutableLiveData<>();

    public static  int endIndexAll = 20;

    public static  int endIndexPending = 20;

    public static  int endIndexCompleted = 20;

    public int getEndIndexAll(){
        return  endIndexAll;
    }

    public void setEndIndexAll(int value){
        endIndexAll = value;
    }

    public int getEndIndexPending(){
        return  endIndexPending;
    }

    public void setEndIndexPending(int value){
        endIndexPending = value;
    }

    public int getEndIndexCompleted(){
        return  endIndexCompleted;
    }

    public void setEndIndexCompleted(int value){
        endIndexCompleted = value;
    }
    public MutableLiveData<String> keyTranfer = new MutableLiveData<>();

    public TodoViewModel(Application application) {
        super(application);
        mRepository = new TodoRepository(application);
        keyTranfer.postValue("");
        checkedList.postValue(new ArrayList<>());
        uncheckedList.postValue(new ArrayList<>());
    }

    public MutableLiveData<String> getKeyTranfer() {
        return keyTranfer;
    }

    public MutableLiveData<List<Todo>> getCheckedList() {
        return checkedList;
    }

    public MutableLiveData<List<Todo>> getUncheckedList() {
        return uncheckedList;
    }

    public LiveData<List<Todo>> getListTodoAll() {
        mTodos = mRepository.getListTodoAll(keyTranfer.getValue());
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

    public Completable deleteCheckedTodos() {
        List<Integer> ids = new ArrayList<>();

        for (Todo e : Objects.requireNonNull(checkedList.getValue())) {
            ids.add(e.getId());
            if (e.getStatus().equals("Completd")){
                if(endIndexCompleted > 0) endIndexCompleted--;
            }else{
                if(endIndexPending > 0) endIndexPending--;
            }
            if(endIndexAll > 0) endIndexAll--;

        }
        return mRepository.deleteCheckedTodos(ids);
    }

    public void setCheckItem(Todo todoT, boolean check) {
        List<Todo> item = checkedList.getValue();
        assert item != null;
        if (check) {
            if (!item.contains(todoT)) {
                item.add(todoT);
            }
        } else {
            if(item.removeIf(a -> a.equals(todoT))){
                setUncheckItem(todoT,true);
            }
        }
        checkedList.postValue(item);
    }

    public void setUncheckItem(Todo todoT, boolean check) {
        List<Todo> item = uncheckedList.getValue();
        assert item != null;
        if (check) {
            if (!item.contains(todoT)) {
                item.add(todoT);
            }
        } else {
            item.removeIf(a -> a.equals(todoT));
        }
        uncheckedList.postValue(item);
    }

    public void resetCheckedList(){
        List<Todo> item = new ArrayList<>();
        checkedList.postValue(item);
    }
    public void resetUncheckedList(){
        List<Todo> item = new ArrayList<>();
        uncheckedList.postValue(item);
    }

}
