package com.example.todo_app.views.java.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
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
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TodoEditFragment extends Fragment {

    private FragmentTodoEditBinding fragmentTodoEditBinding;
    private TodoViewModel todoViewModel;
    private MaterialDatePicker<Long> datePicker = null;
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

        fragmentTodoEditBinding.tieEditCreatedDate.setOnClickListener(view13 -> showDatePicker(fragmentTodoEditBinding.tieEditCreatedDate));

        fragmentTodoEditBinding.tieEditCompletedDate.setOnClickListener(view14 -> showDatePicker(fragmentTodoEditBinding.tieEditCompletedDate));

        fragmentTodoEditBinding.btnEditTodo.setOnClickListener(view1 -> {

            Todo todo = new Todo();

            todo.setId(todoItem.getId());
            todo.setTitle(Objects.requireNonNull(fragmentTodoEditBinding.tieEditTitle.getText()).toString());
            todo.setStatus(fragmentTodoEditBinding.tieEditStatus.getText().toString().trim());
            todo.setDescription(Objects.requireNonNull(fragmentTodoEditBinding.tieEditDescription.getText()).toString());
            todo.setCreatedDate(Objects.requireNonNull(fragmentTodoEditBinding.tieEditCreatedDate.getText()).toString());
            todo.setCompletedDate(Objects.requireNonNull(fragmentTodoEditBinding.tieEditCompletedDate.getText()).toString());

            if (isNullOrEmpty(todo.getTitle()) || isNullOrEmpty(todo.getStatus())
                    || isNullOrEmpty(todo.getDescription()) || isNullOrEmpty(todo.getCreatedDate())
                    || isNullOrEmpty(todo.getCompletedDate())) {
                HelperFunctions.helpers
                        .showSnackBar(view1, "Some fields are missing!"
                                , 255, 51, 51);
            } else {
                try {
                    CompositeDisposable compositeDisposable = new CompositeDisposable();
                    compositeDisposable.add(todoViewModel
                            .updateTodo(todo)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                HelperFunctions.helpers
                                        .showSnackBar(view1, "Data has been updated successfully!"
                                                , 25, 135, 84);
                                Navigation.findNavController(requireView()).popBackStack(R.id.todoListFragment, true);
                                compositeDisposable.dispose();
                            })
                    );
                } catch (Exception e) {
                    HelperFunctions.helpers
                            .showSnackBar(view1, e.toString()
                                    , 255, 51, 51);
                }
            }
        });

        fragmentTodoEditBinding.btnDeleteEditTodo.setOnClickListener(view12 -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle("Confirmation dialog")
                    .setMessage("Do you really want to delete this todo?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        CompositeDisposable compositeDisposable = new CompositeDisposable();
                        compositeDisposable.add(todoViewModel
                                .deleteTodo(todoItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    HelperFunctions.helpers
                                            .showSnackBar(view12, "Data been deleted successfully!"
                                                    , 25, 135, 84);
                                    todoViewModel.setCheckItem(todoItem, false);
                                    todoViewModel.setUncheckItem(todoItem, false);
                                    Navigation.findNavController(requireView()).popBackStack(R.id.todoListFragment, true);
                                    compositeDisposable.dispose();
                                })
                        );
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {

                    })
                    .setCancelable(false)
                    .show();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Transition transition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.trainsition_main);
        setSharedElementEnterTransition(transition);
        fragmentTodoEditBinding = FragmentTodoEditBinding.inflate(inflater, container, false);
        View mView = fragmentTodoEditBinding.getRoot();
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        fragmentTodoEditBinding.setTodoEditViewModel(todoViewModel);

        return mView;
    }

    private boolean isNullOrEmpty(String s) {
        if (s == null) {
            return true;
        } else {
            return s.isEmpty() || s.trim().isEmpty();
        }
    }

    private void showDatePicker(TextInputEditText textDate) {

        if (datePicker == null)
            datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

        datePicker.show(getParentFragmentManager(), "Material_Date_Picker");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = format.format(calendar.getTime());
            textDate.setText(formattedDate);
        });
    }
}