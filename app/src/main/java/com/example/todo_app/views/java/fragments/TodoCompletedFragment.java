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
import com.example.todo_app.databinding.FragmentTodoCompletedBinding;
import com.example.todo_app.models.Todo;
import com.example.todo_app.view_models.TodoViewModel;

import java.util.ArrayList;
import java.util.List;

public class TodoCompletedFragment extends Fragment {


    private FragmentTodoCompletedBinding fragmentTodoCompletedBinding;
    private final TodoViewModel todoViewModel;
    private TodoAdapter todoAdapter;

    private List<Todo> todoList;

    public TodoCompletedFragment(TodoViewModel todoViewModel) {
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
        fragmentTodoCompletedBinding = FragmentTodoCompletedBinding.inflate(inflater, container, false);
        View mView = fragmentTodoCompletedBinding.getRoot();
        fragmentTodoCompletedBinding.setTodoCompletedViewModel(todoViewModel);
        return mView;
    }

    private void loadTodo() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        fragmentTodoCompletedBinding.rclvTodoCompleted.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext()
                , DividerItemDecoration.VERTICAL);
        fragmentTodoCompletedBinding.rclvTodoCompleted.addItemDecoration(dividerItemDecoration);

        todoAdapter = new TodoAdapter(todoList, new TodoAdapter.IClickListener() {
            @Override
            public void onGoDetailItem(Todo todo) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("editItem", todo);
                Navigation.findNavController(requireView()).navigate(R.id.todoEditFragment, bundle
                        , null, null);
            }
        }, todoViewModel);

        fragmentTodoCompletedBinding.rclvTodoCompleted.setAdapter(todoAdapter);

        if (todoList.size() > 0) {
            fragmentTodoCompletedBinding.rclvTodoCompleted.setVisibility(View.VISIBLE);
            fragmentTodoCompletedBinding.isLoadingTodoCompleted.setVisibility(View.INVISIBLE);
        } else {
            fragmentTodoCompletedBinding.rclvTodoCompleted.setVisibility(View.INVISIBLE);
            fragmentTodoCompletedBinding.isLoadingTodoCompleted.setVisibility(View.VISIBLE);
        }

        todoViewModel.getKeyTranfer().observe(requireActivity(), s ->
                todoViewModel.getListTodoByStatus("Completed").observe(requireActivity(), items -> {
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

                    fragmentTodoCompletedBinding.rclvTodoCompleted.setAdapter(todoAdapter);

                    if (todoList.size() > 0) {
                        fragmentTodoCompletedBinding.rclvTodoCompleted.setVisibility(View.VISIBLE);
                        fragmentTodoCompletedBinding.isLoadingTodoCompleted.setVisibility(View.INVISIBLE);
                    } else {
                        fragmentTodoCompletedBinding.rclvTodoCompleted.setVisibility(View.INVISIBLE);
                        fragmentTodoCompletedBinding.isLoadingTodoCompleted.setVisibility(View.VISIBLE);
                    }

                }));
    }
}