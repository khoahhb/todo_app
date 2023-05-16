package com.example.todo_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.todo_app.ViewModels.TodoViewModel;
import com.example.todo_app.databinding.ActivityMainBinding;
import com.example.todo_app.models.Todo;

public class MainActivity extends AppCompatActivity {

    private TodoViewModel todoViewModel;

    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);

        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Todo todo = new Todo("1","2","3","4","5");

                try {
                    todoViewModel.insert(todo);
                    Toast.makeText(MainActivity.this, "Data has been saved successfully!", Toast.LENGTH_LONG).show();
                }catch (Exception e){

                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}