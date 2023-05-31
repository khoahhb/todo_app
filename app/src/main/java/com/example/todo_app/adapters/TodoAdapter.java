package com.example.todo_app.adapters;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo_app.databinding.ItemLoadingBinding;
import com.example.todo_app.databinding.TodoItemBinding;
import com.example.todo_app.models.Todo;
import com.example.todo_app.view_models.TodoViewModel;
import com.example.todo_app.R;

import java.util.List;
import java.util.Objects;

public class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;

    private static final int TYPE_LOADING = 2;

    private boolean isLoadingAdd;

    private List<Todo> mTodoList;

    private final TodoClickListener mTodoClickListener;

    private final TodoViewModel todoViewModel;

    public interface TodoClickListener {
        void onGoDetailItem(Todo todo, CardView cardView);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Todo> list) {
        this.mTodoList = list;
        notifyDataSetChanged();
    }
    public void addData(List<Todo> list) {
        int listSize = this.mTodoList.size();

        int addSize = list.size();

        for(int i = 0 ;i <addSize; i ++){
            this.mTodoList.add(list.get(i));
            notifyItemInserted(listSize + i);
        }
    }

    public void loadChangedData() {
        if (mTodoList != null) {
            List<Todo> uncheckedList = todoViewModel.getUncheckedList().getValue();
            if (uncheckedList != null) {
                int size = uncheckedList.size();

                for (int i = 0; i < size; i++) {
                    notifyItemChanged(mTodoList.indexOf(uncheckedList.get(i)));
                }

                todoViewModel.resetUncheckedList();
            }

            List<Todo> checkedList = todoViewModel.getCheckedList().getValue();
            if (checkedList != null){
                int size = checkedList.size();

                for (int i = 0; i < size; i++) {
                    notifyItemChanged(mTodoList.indexOf(checkedList.get(i)));
                }
            }
        }
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
            TodoItemBinding itemTodoBinding = TodoItemBinding.inflate(LayoutInflater
                    .from(parent.getContext()), parent, false);
            return new TodoViewHolder(itemTodoBinding);
        } else {
            ItemLoadingBinding itemLoadingBinding = ItemLoadingBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new LoadingViewHolder(itemLoadingBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_ITEM) {
            Todo todo = mTodoList.get(position);
            TodoViewHolder todoViewHolder = (TodoViewHolder) holder;
            Animation animation = AnimationUtils.loadAnimation(todoViewHolder.itemView.getContext(), R.anim.enlarge);
            todoViewHolder.bind(todo);
            todoViewHolder.itemView.startAnimation(animation);
        }
    }

    @Override
    public int getItemCount() {
        if (mTodoList != null) {
            return mTodoList.size();
        }
        return 0;
    }

    public TodoAdapter(List<Todo> mTodoList, TodoClickListener mTodoClickListener,
                       TodoViewModel todoViewModel) {
        this.mTodoList = mTodoList;
        this.mTodoClickListener = mTodoClickListener;
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

            List<Todo> checkedList = todoViewModel.getCheckedList().getValue();

            if(Objects.requireNonNull(checkedList).contains(todo)){
                todoItemBinding.ckbTitle.setChecked(true);
                todoItemBinding.ckbTitle.setPaintFlags(todoItemBinding.ckbTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                todoItemBinding.ckbTitle.setChecked(false);
                todoItemBinding.ckbTitle.setPaintFlags(todoItemBinding.ckbTitle.getPaintFlags() & ~ Paint.STRIKE_THRU_TEXT_FLAG);
            }

            todoItemBinding.btnGoDetail.setOnClickListener(view -> mTodoClickListener.onGoDetailItem(todo, todoItemBinding.cardItem));

            todoItemBinding.cardItem.setOnClickListener(view -> {

                todoItemBinding.ckbTitle.setChecked(!todoItemBinding.ckbTitle.isChecked());
                if (todoItemBinding.ckbTitle.isChecked()) {
                    todoItemBinding.ckbTitle.setPaintFlags(todoItemBinding.ckbTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    todoViewModel.setCheckItem(todo,true);
                } else {
                    todoItemBinding.ckbTitle.setPaintFlags(todoItemBinding.ckbTitle.getPaintFlags() & ~ Paint.STRIKE_THRU_TEXT_FLAG);
                    todoViewModel.setCheckItem(todo,false);
                }

            });

            todoItemBinding.ckbTitle.setOnClickListener(view -> {
                if (todoItemBinding.ckbTitle.isChecked()) {
                    todoItemBinding.ckbTitle.setPaintFlags(todoItemBinding.ckbTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    todoViewModel.setCheckItem(todo,true);
                } else {
                    todoItemBinding.ckbTitle.setPaintFlags(todoItemBinding.ckbTitle.getPaintFlags() & ~ Paint.STRIKE_THRU_TEXT_FLAG);
                    todoViewModel.setCheckItem(todo,false);
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
