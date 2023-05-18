package com.example.todo_app.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo_app.R;
import com.example.todo_app.models.Todo;
import com.example.todo_app.view_models.TodoViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<Todo> mTodoList;
    private List<Todo> checkedList;
    private IClickListener mIClickListener;
    private TodoViewModel todoViewModel;

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.todo_item, parent, false);
        return new TodoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = mTodoList.get(position);
        if (todo == null) {
            return;
        }
        holder.bind(todo);
    }

    @Override
    public int getItemCount() {
        if (mTodoList != null) {
            return mTodoList.size();
        }
        return 0;
    }

    public interface IClickListener {
        void onGoDetailItem(Todo todo);
    }

    public TodoAdapter(List<Todo> mTodoList, IClickListener mIClickListener,
                       TodoViewModel todoViewModel) {
        this.mTodoList = mTodoList;
        this.checkedList = new ArrayList<>();
        this.mIClickListener = mIClickListener;
        this.todoViewModel = todoViewModel;
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {
        private CheckBox ckbTitle;
        private MaterialButton btnGoDetail;
        public TodoViewHolder(View view) {
            super(view);
            ckbTitle = view.findViewById(R.id.ckbTitle);
            btnGoDetail = view.findViewById(R.id.btnGoDetail);
        }
        public void bind(Todo todo) {
            ckbTitle.setText(todo.getTitle());
            btnGoDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mIClickListener.onGoDetailItem(todo);
                }
            });
            ckbTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (ckbTitle.isChecked()) {
                        ckbTitle.setPaintFlags(
                                ckbTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                        );
                        if (!checkedList.contains(todo))
                            checkedList.add(todo);
                    } else {
                        ckbTitle.setPaintFlags(
                                ckbTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
                        );
                        if (checkedList.contains(todo))
                            checkedList.remove(todo);
                    }
                    todoViewModel.checkedList = checkedList;
                }
            });
        }

    }
}
