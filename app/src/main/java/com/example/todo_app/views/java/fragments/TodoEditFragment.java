package com.example.todo_app.views.java.fragments;

import android.content.DialogInterface;
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
import com.example.todo_app.databinding.FragmentTodoEditBinding;
import com.example.todo_app.helpers.HelperFunctions;
import com.example.todo_app.models.Todo;
import com.example.todo_app.view_models.TodoViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TodoEditFragment extends Fragment {

    private FragmentTodoEditBinding fragmentTodoEditBinding;
    private TodoViewModel todoViewModel;
    private MaterialDatePicker<Long> datePickerCompleted;
    private MaterialDatePicker<Long> datePickerCreated;
    private Todo todoItem;

    public TodoEditFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert getArguments() != null;
        todoItem = (Todo) getArguments().getSerializable("editItem");

        if (todoItem != null) {
            fragmentTodoEditBinding.tieEditTitle.setText(todoItem.getTitle());
            fragmentTodoEditBinding.tieEditDescription.setText(todoItem.getDescription());
            fragmentTodoEditBinding.tieEditCreatedDate.setText(todoItem.getCreatedDate());
            fragmentTodoEditBinding.tieEditCompletedDate.setText(todoItem.getCompletedDate());
            fragmentTodoEditBinding.tieEditStatus.setText(todoItem.getStatus(), false);
        }

        fragmentTodoEditBinding.tieEditCreatedDate.setOnClickListener(view13 -> {
            if (datePickerCreated.isAdded()) {
                return;
            }
            datePickerCreated.show(getParentFragmentManager(), "Material_Date_Picker2");
            datePickerCreated.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyy", Locale.getDefault());
                String formattedDate = format.format(new Date(selection));
                fragmentTodoEditBinding.tieEditCreatedDate.setText(formattedDate);
            });
        });

        fragmentTodoEditBinding.tieEditCompletedDate.setOnClickListener(view14 -> {
            if (datePickerCompleted.isAdded()) {
                return;
            }
            datePickerCompleted.show(getParentFragmentManager(), "Material_Date_Picker");
            datePickerCompleted.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyy", Locale.getDefault());
                String formattedDate = format.format(new Date(selection));
                fragmentTodoEditBinding.tieEditCompletedDate.setText(formattedDate);
            });
        });

        fragmentTodoEditBinding.btnEditTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Todo todo = new Todo();

                todo.setId(todoItem.getId());
                todo.setTitle(fragmentTodoEditBinding.tieEditTitle.getText().toString());
                todo.setStatus(fragmentTodoEditBinding.tieEditStatus.getText().toString().trim());
                todo.setDescription(fragmentTodoEditBinding.tieEditDescription.getText().toString());
                todo.setCreatedDate(fragmentTodoEditBinding.tieEditCreatedDate.getText().toString());
                todo.setCompletedDate(fragmentTodoEditBinding.tieEditCompletedDate.getText().toString());

                if (isNullOrEmpty(todo.getTitle()) || isNullOrEmpty(todo.getStatus())
                        || isNullOrEmpty(todo.getDescription()) || isNullOrEmpty(todo.getCreatedDate())
                        || isNullOrEmpty(todo.getCompletedDate())) {
                    HelperFunctions.helpers
                            .showSnackBar(view, "Some fields are missing!"
                                    ,255, 51, 51);
                } else {
                    try {
                        CompositeDisposable compositeDisposable = new CompositeDisposable();
                        compositeDisposable.add(todoViewModel
                                .updateTodo(todo)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    HelperFunctions.helpers
                                            .showSnackBar(view, "Data has been updated successfully!"
                                                    ,25, 135, 84);
                                    Navigation.findNavController(getView()).navigate(R.id.todoListFragment);
                                    compositeDisposable.dispose();
                                })
                        );
                    } catch (Exception e) {
                        HelperFunctions.helpers
                                .showSnackBar(view, e.toString()
                                        ,255, 51, 51);
                    }
                }
            }
        });

        fragmentTodoEditBinding.btnDeleteEditTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setTitle("Confirmation dialog")
                        .setMessage("Do you really want to delete this todo?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CompositeDisposable compositeDisposable = new CompositeDisposable();
                                compositeDisposable.add(todoViewModel
                                        .deleteTodo(todoItem)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            HelperFunctions.helpers
                                                    .showSnackBar(view, "Data been deleted successfully!"
                                                            ,25, 135, 84);
                                            Navigation.findNavController(getView()).navigate(R.id.todoListFragment
                                                    , null, null, null);
                                            compositeDisposable.dispose();
                                        })
                                );
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        fragmentTodoEditBinding.tieEditTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Objects.requireNonNull(fragmentTodoEditBinding.tieEditTitle.getText())
                        .toString().trim().isEmpty()) {
                    fragmentTodoEditBinding.tilEditTitle.setError("Field title can't empty");
                } else {
                    fragmentTodoEditBinding.tilEditTitle.setError(null);
                }
            }
        });

        fragmentTodoEditBinding.tieEditDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Objects.requireNonNull(fragmentTodoEditBinding.tieEditDescription
                        .getText()).toString().trim().isEmpty()) {
                    fragmentTodoEditBinding.tilEditDescription.setError("Description title can't empty");
                } else {
                    fragmentTodoEditBinding.tilEditDescription.setError(null);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentTodoEditBinding = FragmentTodoEditBinding.inflate(inflater, container, false);
        View mView = fragmentTodoEditBinding.getRoot();
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        todoViewModel.resetCheckedList();
        fragmentTodoEditBinding.setTodoEditViewModel(todoViewModel);

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
}