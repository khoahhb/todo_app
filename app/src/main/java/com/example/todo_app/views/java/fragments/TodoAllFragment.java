package com.example.todo_app.views.java.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todo_app.R;
import com.example.todo_app.adapters.PaginationScrollListener;
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
    private List<Todo> todoAllList;
    private List<Todo> todoMainList;
    private boolean isLoading;
    private boolean isLastPage;
    private int totalPage = 5;
    private int startIndex;
    private int endIndex;
    private int currentPage = 1;

    public TodoAllFragment(TodoViewModel todoViewModel) {
        this.todoViewModel = todoViewModel;
    }

    @Override
    public void onResume() {
        super.onResume();
        todoAdapter.loadChangedData();
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

        todoAdapter = new TodoAdapter(todoAllList, new TodoAdapter.IClickListener() {
            @Override
            public void onGoDetailItem(Todo todo) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("editItem", todo);
                Navigation.findNavController(requireView()).navigate(R.id.todoEditFragment
                        , bundle, null, null);
            }
        }, todoViewModel);

        fragmentTodoAllBinding.rclvTodoAll.setAdapter(todoAdapter);

        todoViewModel.getKeyTranfer().observe(requireActivity(), s ->
                todoViewModel.getListTodoAll(todoViewModel.getKeyTranfer().getValue())
                        .observe(requireActivity(), items -> {
                    todoAllList = items;
                    currentPage = 0;
                    isLastPage = false;
                    if (items.size() % 10 == 0) {
                        totalPage = (items.size() / 10);
                    } else {
                        totalPage = (items.size() / 10) + 1;
                    }
                    loadFirstPage();
                }));

        fragmentTodoAllBinding.rclvTodoAll.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMore() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();
            }
            @Override
            public boolean isLoading() {
                return isLoading;
            }
            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
        });
    }

    private void loadFirstPage() {
        fragmentTodoAllBinding.isLoadingTodoAll.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            startIndex = 0;
            endIndex = 10;
            if (todoAllList.size() > 10) {
                todoMainList = todoAllList.subList(startIndex, endIndex);
                startIndex = endIndex;
                if ((endIndex + 10) < todoAllList.size()) {
                    endIndex += 10;
                }
            } else {
                todoMainList = todoAllList;
            }
            todoAdapter.setData(todoMainList);
            if (currentPage < totalPage) {
                todoAdapter.addFooterLoading();
            } else {
                isLastPage = true;
            }
            fragmentTodoAllBinding.isLoadingTodoAll.setVisibility(View.INVISIBLE);
        }, 1000);
    }

    private void loadNextPage() {
        new Handler().postDelayed(() -> {
            List<Todo> list = new ArrayList<>();
            if (todoAllList.size() > 10) {
                list = todoAllList.subList(startIndex, endIndex);

                startIndex = endIndex;
                if ((endIndex + 10) < todoAllList.size()) {
                    endIndex += 10;
                } else {
                    endIndex = todoAllList.size();
                }
            }
            todoAdapter.removeFooterLoading();
            todoMainList.addAll(list);
            todoAdapter.setData(todoMainList);
            isLoading = false;
            if (currentPage < totalPage) {
                todoAdapter.addFooterLoading();
            } else {
                isLastPage = true;
            }
        }, 1000);
    }
}