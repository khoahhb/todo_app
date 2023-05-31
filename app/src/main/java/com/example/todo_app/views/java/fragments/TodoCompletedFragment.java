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
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.example.todo_app.R;
import com.example.todo_app.adapters.PaginationScrollListener;
import com.example.todo_app.adapters.TodoAdapter;
import com.example.todo_app.databinding.FragmentTodoCompletedBinding;
import com.example.todo_app.helpers.CustomLinearLayoutManager;
import com.example.todo_app.models.Todo;
import com.example.todo_app.view_models.TodoViewModel;

import java.util.ArrayList;
import java.util.List;

public class TodoCompletedFragment extends Fragment {

    private FragmentTodoCompletedBinding fragmentTodoCompletedBinding;
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

    public TodoCompletedFragment(TodoViewModel todoViewModel) {
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentTodoCompletedBinding = FragmentTodoCompletedBinding.inflate(inflater, container, false);
        View mView = fragmentTodoCompletedBinding.getRoot();
        fragmentTodoCompletedBinding.setTodoCompletedViewModel(todoViewModel);
        return mView;
    }

    private void loadTodo() {

        View customItemView = LayoutInflater.from(getContext()).inflate(R.layout.todo_item, null);
        customItemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int itemHeight = customItemView.getMeasuredHeight();

        CustomLinearLayoutManager layoutManager = new CustomLinearLayoutManager(getContext(), itemHeight*10, itemHeight*10);
        fragmentTodoCompletedBinding.rclvTodoCompleted.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext()
                , DividerItemDecoration.VERTICAL);
        fragmentTodoCompletedBinding.rclvTodoCompleted.addItemDecoration(dividerItemDecoration);

        todoAdapter = new TodoAdapter(todoAllList, (todo, cardView) -> {
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(cardView, "edit_fragment").build();
            Bundle bundle = new Bundle();
            bundle.putSerializable("editItem", todo);
            Navigation.findNavController(requireView()).navigate(R.id.todoEditFragment
                    , bundle, null, extras);
        }, todoViewModel);

        fragmentTodoCompletedBinding.rclvTodoCompleted.setAdapter(todoAdapter);

        todoViewModel.getKeyTransfer().observe(requireActivity(), s ->
                todoViewModel.getListTodoByStatus("Completed").observe(requireActivity(), items -> {
                    todoAllList = items;
                    currentPage = 0;
                    isLastPage = false;
                    if (items.size() % 30 == 0) {
                        totalPage = (items.size() / 30);
                    } else {
                        totalPage = (items.size() / 30) + 1;
                    }
                    loadFirstPage();
                }));

        fragmentTodoCompletedBinding.rclvTodoCompleted.addOnScrollListener(new PaginationScrollListener(layoutManager) {
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
        fragmentTodoCompletedBinding.isLoadingTodoCompleted.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            startIndex = 0;
            endIndex = 30;
            if (todoAllList.size() > 30) {

                todoMainList = new ArrayList<>(todoAllList.subList(startIndex, endIndex));
                startIndex = endIndex;
                endIndex += 30;
                if (todoAllList.size() - 1 < endIndex) {
                    endIndex = todoAllList.size() - 1;
                }
            } else {
                todoMainList = new ArrayList<>(todoAllList);
            }
            todoAdapter.setData(todoMainList);
            if (currentPage < totalPage && todoAllList.size() > 30 ) {
                todoAdapter.addFooterLoading();
            } else {
                isLastPage = true;
            }
            fragmentTodoCompletedBinding.isLoadingTodoCompleted.setVisibility(View.INVISIBLE);
        }, 1000);
    }

    private void loadNextPage() {
        new Handler().postDelayed(() -> {

            List<Todo> list;

            if(todoAllList.size() > todoMainList.size()){
                list = new ArrayList<>(todoAllList.subList(startIndex, endIndex));
                startIndex = endIndex;
                endIndex += 30;
                if (todoAllList.size() - 1 < endIndex) {
                    endIndex = todoAllList.size() - 1;
                }
                todoAdapter.removeFooterLoading();
                todoAdapter.addData(list);
                isLoading = false;
                if (currentPage < totalPage) {
                    todoAdapter.addFooterLoading();
                } else {
                    isLastPage = true;
                }
            }
        }, 1000);
    }
}