package com.example.todo_app.views.java.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todo_app.R;
import com.example.todo_app.adapters.TodoAdapter;
import com.example.todo_app.databinding.FragmentTodoAllBinding;
import com.example.todo_app.models.Todo;
import com.example.todo_app.view_models.TodoViewModel;

import java.util.ArrayList;
import java.util.List;

public class TodoAllFragment extends Fragment {

    private FragmentTodoAllBinding fragmentTodoAllBinding;
    private final TodoViewModel todoViewModel;
    private TodoAdapter todoAdapter;

    private List<Todo> todoList;

    public TodoAllFragment(TodoViewModel todoViewModel) {
        this.todoViewModel = todoViewModel;
        this.todoViewModel.resetCheckedList();
        todoList = new ArrayList<>();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadTodo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentTodoAllBinding = FragmentTodoAllBinding.inflate(inflater, container, false);
        View mView = fragmentTodoAllBinding.getRoot();
        fragmentTodoAllBinding.setTodoAllViewModel(todoViewModel);
        return mView;
    }

    private void loadTodo() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        fragmentTodoAllBinding.rclvTodoAll.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext()
                , DividerItemDecoration.VERTICAL);
        fragmentTodoAllBinding.rclvTodoAll.addItemDecoration(dividerItemDecoration);

        todoViewModel.getKeyTranfer().observe(requireActivity(), s ->
                todoViewModel.getListTodoAll().observe(requireActivity(), items -> {
                    if (todoList.size() > 0) {
                        todoList.clear();
                    }
                    todoList.addAll(items);

                    todoAdapter = new TodoAdapter(todoList, new TodoAdapter.IClickListener() {
                        @Override
                        public void onGoDetailItem(Todo todo) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("editItem", todo);
                            Navigation.findNavController(requireView()).navigate(R.id.todoEditFragment
                                    , bundle, null, null);
                        }
                    }, todoViewModel);

                    fragmentTodoAllBinding.rclvTodoAll.setAdapter(todoAdapter);

                    if (todoList.size() > 0) {
                        fragmentTodoAllBinding.rclvTodoAll.setVisibility(View.VISIBLE);
                        fragmentTodoAllBinding.isLoadingTodoAll.setVisibility(View.INVISIBLE);
                    } else {
                        fragmentTodoAllBinding.rclvTodoAll.setVisibility(View.INVISIBLE);
                        fragmentTodoAllBinding.isLoadingTodoAll.setVisibility(View.VISIBLE);
                    }

                }));
    }
}