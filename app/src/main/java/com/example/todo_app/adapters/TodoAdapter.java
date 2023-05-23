package com.example.todo_app.adapters;

import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo_app.R;
import com.example.todo_app.databinding.ItemLoadingBinding;
import com.example.todo_app.databinding.TodoItemBinding;
import com.example.todo_app.models.Todo;
import com.example.todo_app.view_models.TodoViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADING = 2;
    private boolean isLoadingAdd;

    private List<Todo> mTodoList;
    private IClickListener mIClickListener;
    private TodoViewModel todoViewModel;

    public interface IClickListener {
        void onGoDetailItem(Todo todo);
    }

    public void setData(List<Todo> list) {
        this.mTodoList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mTodoList != null && position == mTodoList.size() - 1 && isLoadingAdd) {
            return TYPE_LOADING;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (TYPE_ITEM == viewType) {
            TodoItemBinding itemTodoBinding = TodoItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new TodoViewHolder(itemTodoBinding);
        } else {
            ItemLoadingBinding itemLoadingBinding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new LoadingViewHolder(itemLoadingBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_ITEM) {
            Todo todo = mTodoList.get(position);
            TodoViewHolder todoViewHolder = (TodoViewHolder) holder;
            todoViewHolder.bind(todo);
        }
    }

    @Override
    public int getItemCount() {
        if (mTodoList != null) {
            return mTodoList.size();
        }
        return 0;
    }


    public TodoAdapter(List<Todo> mTodoList, IClickListener mIClickListener,
                       TodoViewModel todoViewModel) {
        this.mTodoList = mTodoList;
        this.mIClickListener = mIClickListener;
        this.todoViewModel = todoViewModel;
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {

        private final TodoItemBinding todoItemBinding;

        public TodoViewHolder(@NonNull TodoItemBinding itemTodoBinding) {
            super(itemTodoBinding.getRoot());
            this.todoItemBinding = itemTodoBinding;
        }

        public void bind(Todo todo) {

            todoItemBinding.ckbTitle.setText(todo.getTitle());
            todoItemBinding.ckbTitle.setChecked(false);

            List<Todo> checkedList = todoViewModel.getChekedList().getValue();

            todoItemBinding.btnGoDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mIClickListener.onGoDetailItem(todo);
                }
            });
            todoItemBinding.ckbTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (todoItemBinding.ckbTitle.isChecked()) {

                        if (!checkedList.contains(todo))
                            checkedList.add(todo);
                    } else {
                        todoItemBinding.ckbTitle.setPaintFlags(
                                todoItemBinding.ckbTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
                        );
                        if (checkedList.contains(todo))
                            checkedList.remove(todo);
                    }
                    todoViewModel.checkedList.postValue(checkedList);
                }
            });
        }

    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull ItemLoadingBinding itemLoadingBinding) {
            super(itemLoadingBinding.getRoot());
        }
    }

    public void addFooterLoading() {
        isLoadingAdd = true;
    }

    public void removeFooterLoading() {
        isLoadingAdd = false;
    }
}
