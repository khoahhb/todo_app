package com.example.todo_app.views.java.fragments;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.todo_app.R;
import com.example.todo_app.adapters.TabTodoAdapter;
import com.example.todo_app.databinding.FragmentTodoListBinding;
import com.example.todo_app.helpers.HelperFunctions;
import com.example.todo_app.view_models.TodoViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TodoListFragment extends Fragment {

    private FragmentTodoListBinding fragmentTodoListBinding;
    private TodoViewModel todoViewModel;

    public TodoListFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentTodoListBinding.btnGoAdd.setOnClickListener(view1 -> {
            Navigation.findNavController(view1).navigate(R.id.todoAddFragment,
                    null, null, null);
        });

        ViewPager2 viewPager2 = requireView().findViewById(R.id.vpgContent);
        fragmentTodoListBinding.vpgContent.setAdapter(new TabTodoAdapter(requireActivity(), todoViewModel));
        TabLayout tabLayout = requireView().findViewById(R.id.tabStatus);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("All");
                    break;
                case 1:
                    tab.setText("Pending");
                    break;
                case 2:
                    tab.setText("Completed");
                    break;
            }
        });

        tabLayoutMediator.attach();

        fragmentTodoListBinding.btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (todoViewModel.checkedList.size() == 0) {
                    HelperFunctions.helpers
                            .showSnackBar(view, "Nothing is selected!"
                                    ,255, 191, 0);
                } else {
                    deleteSelectedTodos();
                }
            }
        });

        fragmentTodoListBinding.svSearchTodo
                .getEditText()
                .setOnEditorActionListener(
                        (v, actionId, event) -> {
                            fragmentTodoListBinding.sbSearchTodo
                                    .setText(fragmentTodoListBinding.svSearchTodo.getText());
                            fragmentTodoListBinding.svSearchTodo.hide();
                            todoViewModel.getKeyTranfer()
                                    .setValue(Objects
                                            .requireNonNull(fragmentTodoListBinding.svSearchTodo
                                                    .getText()).toString());
                            return false;
                        });

        fragmentTodoListBinding.tabStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                fragmentTodoListBinding.svSearchTodo.setText("");
                fragmentTodoListBinding.sbSearchTodo.setText("");
                todoViewModel.getKeyTranfer().setValue(Objects
                        .requireNonNull(fragmentTodoListBinding.svSearchTodo.getText()).toString());
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentTodoListBinding = FragmentTodoListBinding.inflate(inflater, container, false);
        View mView = fragmentTodoListBinding.getRoot();
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        fragmentTodoListBinding.setTodoListViewModel(todoViewModel);
        return mView;
    }

    private void deleteSelectedTodos() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Confirmation dialog")
                .setMessage("Do you really want to delete selected todos?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CompositeDisposable compositeDisposable = new CompositeDisposable();
                        compositeDisposable.add(todoViewModel
                                .deleteSelectedTodos()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    HelperFunctions.helpers
                                            .showSnackBar(getView(), "Data been deleted successfully!"
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
}