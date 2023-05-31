package com.example.todo_app.views.java.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
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
import com.example.todo_app.databinding.FragmentTodoAddBinding;
import com.example.todo_app.helpers.HelperFunctions;
import com.example.todo_app.models.Todo;
import com.example.todo_app.view_models.TodoViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TodoAddFragment extends Fragment {

    private FragmentTodoAddBinding fragmentTodoAddBinding;
    private TodoViewModel todoViewModel;

    private MaterialDatePicker<Long> datePicker = null;

    public TodoAddFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Set transition
        Transition transition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.trainsition_main);
        setSharedElementEnterTransition(transition);

        //Initialize instances
        fragmentTodoAddBinding = FragmentTodoAddBinding.inflate(inflater, container, false);
        View mView = fragmentTodoAddBinding.getRoot();
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        fragmentTodoAddBinding.setTodoAddViewModel(todoViewModel);

        fragmentTodoAddBinding.tieAddCreatedDate
                .setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
        fragmentTodoAddBinding.tieAddCompletedDate
                .setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //Show date picker
        fragmentTodoAddBinding.tieAddCreatedDate.setOnClickListener(view13 -> showDatePicker(fragmentTodoAddBinding.tieAddCreatedDate));

        fragmentTodoAddBinding.tieAddCompletedDate.setOnClickListener(view13 -> showDatePicker(fragmentTodoAddBinding.tieAddCompletedDate));

        //Handle add button
        fragmentTodoAddBinding.btnAddTodo.setOnClickListener(view1 -> {

            Todo todo = new Todo();

            todo.setTitle(Objects.requireNonNull(fragmentTodoAddBinding.tieAddTitle.getText()).toString());
            todo.setStatus(fragmentTodoAddBinding.tieAddStatus.getText().toString().trim());
            todo.setDescription(Objects.requireNonNull(fragmentTodoAddBinding.tieAddDescription.getText()).toString());
            todo.setCreatedDate(Objects.requireNonNull(fragmentTodoAddBinding.tieAddCreatedDate.getText()).toString());
            todo.setCompletedDate(Objects.requireNonNull(fragmentTodoAddBinding.tieAddCompletedDate.getText()).toString());

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
                            .insertTodo(todo)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                HelperFunctions.helpers
                                        .showSnackBar(view1, "Data has been saved successfully!"
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

        //Handle add test data
        fragmentTodoAddBinding.btnAddTestData.setOnClickListener(view12 -> {
            try {

                for (int i = 1; i < 300; i++) {
                    Todo todoTemp = new Todo("Item " + i, "desc", "Pending",
                            "2023-14-11", "2023-14-11");
                    testFunct(todoTemp);
                }
                Navigation.findNavController(requireView()).popBackStack(R.id.todoListFragment, true);

            } catch (Exception e) {
                HelperFunctions.helpers
                        .showSnackBar(view12, e.toString()
                                , 255, 51, 51);
            }
        });

        //Validate fields
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

    //Show datepicker
    private void showDatePicker(TextInputEditText textDate) {

        if (datePicker == null)
            datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

        datePicker.show(getParentFragmentManager(), "Material_Date_Picker");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = format.format(calendar.getTime());
            textDate.setText(formattedDate);
        });
    }

    private boolean isNullOrEmpty(String s) {
        if (s == null) {
            return true;
        } else {
            return s.isEmpty() || s.trim().isEmpty();
        }
    }

    public void testFunct(Todo todo) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(todoViewModel
                .insertTodo(todo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(compositeDisposable::dispose));
    }
}
