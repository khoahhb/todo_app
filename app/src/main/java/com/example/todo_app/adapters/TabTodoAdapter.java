package com.example.todo_app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.todo_app.view_models.TodoViewModel;
import com.example.todo_app.views.java.fragments.TodoAllFragment;
import com.example.todo_app.views.java.fragments.TodoCompletedFragment;
import com.example.todo_app.views.java.fragments.TodoPendingFragment;

public class TabTodoAdapter extends FragmentStateAdapter {

    private final TodoViewModel todoViewModel;

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TodoAllFragment(todoViewModel);
            case 1:
                return new TodoPendingFragment(todoViewModel);
            default:
                return new TodoCompletedFragment(todoViewModel);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public TabTodoAdapter(@NonNull FragmentActivity fragmentActivity, TodoViewModel todoViewModel) {
        super(fragmentActivity);
        this.todoViewModel = todoViewModel;
    }

}
