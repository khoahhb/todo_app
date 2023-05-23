package com.example.todo_app.views.java.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.todo_app.R;
import com.example.todo_app.databinding.FragmentTodoAddBinding;
import com.example.todo_app.helpers.HelperFunctions;
import com.example.todo_app.models.Todo;
import com.example.todo_app.view_models.TodoViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TodoAddFragment extends Fragment {

    private FragmentTodoAddBinding fragmentTodoAddBinding;
    private TodoViewModel todoViewModel;
    private MaterialDatePicker<Long> datePickerCompleted;
    private MaterialDatePicker<Long> datePickerCreated;

    public TodoAddFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentTodoAddBinding.tieAddCreatedDate.setOnClickListener(view13 -> {
            if (datePickerCreated.isAdded()) {
                return;
            }
            datePickerCreated.show(getParentFragmentManager(), "Material_Date_Picker2");
            datePickerCreated.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyy", Locale.getDefault());
                String formattedDate = format.format(new Date(selection));
                fragmentTodoAddBinding.tieAddCreatedDate.setText(formattedDate);
            });
        });

        fragmentTodoAddBinding.tieAddCompletedDate.setOnClickListener(view14 -> {
            if (datePickerCompleted.isAdded()) {
                return;
            }
            datePickerCompleted.show(getParentFragmentManager(), "Material_Date_Picker");
            datePickerCompleted.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyy", Locale.getDefault());
                String formattedDate = format.format(new Date(selection));
                fragmentTodoAddBinding.tieAddCompletedDate.setText(formattedDate);
            });
        });

        fragmentTodoAddBinding.btnAddTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Todo todo = new Todo();

                todo.setTitle(fragmentTodoAddBinding.tieAddTitle.getText().toString());
                todo.setStatus(fragmentTodoAddBinding.tieAddStatus.getText().toString().trim());
                todo.setDescription(fragmentTodoAddBinding.tieAddDescription.getText().toString());
                todo.setCreatedDate(fragmentTodoAddBinding.tieAddCreatedDate.getText().toString());
                todo.setCompletedDate(fragmentTodoAddBinding.tieAddCompletedDate.getText().toString());
                if (isNullOrEmpty(todo.getTitle()) || isNullOrEmpty(todo.getStatus())
                        || isNullOrEmpty(todo.getDescription()) || isNullOrEmpty(todo.getCreatedDate())
                        || isNullOrEmpty(todo.getCompletedDate())) {
                    HelperFunctions.helpers
                            .showSnackBar(view, "Some fields are missing!"
                                    , 255, 51, 51);
                } else {
                    try {
                        CompositeDisposable compositeDisposable = new CompositeDisposable();
                        compositeDisposable.add(todoViewModel
                                .insertTodo(todo)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    HelperFunctions.helpers
                                            .showSnackBar(view, "Data has been saved successfully!"
                                                    , 25, 135, 84);
                                    Navigation.findNavController(getView()).navigate(R.id.todoListFragment);
                                    compositeDisposable.dispose();
                                })
                        );

                    } catch (Exception e) {
                        HelperFunctions.helpers
                                .showSnackBar(view, e.toString()
                                        , 255, 51, 51);
                    }
                }
            }
        });

        fragmentTodoAddBinding.btnAddTestData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    for (int i = 1; i < 30; i++) {
                        Todo todoTemp = new Todo("Item " + i, "desc", "Pending",
                                "2023-14-11", "2023-14-11");
                        testFunct(todoTemp);
                    }
                    Navigation.findNavController(getView()).navigate(R.id.todoListFragment);

                } catch (Exception e) {
                    HelperFunctions.helpers
                            .showSnackBar(view, e.toString()
                                    , 255, 51, 51);
                }
            }
        });

        fragmentTodoAddBinding.tieAddTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Objects.requireNonNull(fragmentTodoAddBinding.tieAddTitle
                        .getText()).toString().trim().isEmpty()) {
                    fragmentTodoAddBinding.tilAddTitle.setError("Field title can't empty");
                } else {
                    fragmentTodoAddBinding.tilAddTitle.setError(null);
                }
            }
        });

        fragmentTodoAddBinding.tieAddDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Objects.requireNonNull(fragmentTodoAddBinding.tieAddDescription
                        .getText()).toString().trim().isEmpty()) {
                    fragmentTodoAddBinding.tilAddDescription.setError("Description title can't empty");
                } else {
                    fragmentTodoAddBinding.tilAddDescription.setError(null);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentTodoAddBinding = FragmentTodoAddBinding.inflate(inflater, container, false);
        View mView = fragmentTodoAddBinding.getRoot();
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        todoViewModel.resetCheckedList();
        fragmentTodoAddBinding.setTodoAddViewModel(todoViewModel);

        datePickerCreated = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pick created date").setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePickerCompleted = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pick completed date").setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        return mView;
    }

    private boolean isNullOrEmpty(String s) {
        if (s == null) {
            return true;
        } else {
            if (s.isEmpty() || s.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void testFunct(Todo todo) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(todoViewModel
                .insertTodo(todo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    compositeDisposable.dispose();
                }));
    }
}