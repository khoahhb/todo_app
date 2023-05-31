package com.example.todo_app.views.java.activities;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.todo_app.R;

public class FrameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.frame);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        setupActionBarWithNavController(this, navController);
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.frame);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_custom,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.frame);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        switch (item.getItemId()) {
            case R.id.switchScreen:
                navController.navigate(R.id.navActivity,
                        null, null, null);
                return true;
            case R.id.homeScreen:
                navController.navigate(R.id.todoListFragment,
                        null, null, null);
                return true;
            case R.id.addScreen:
                navController.navigate(R.id.todoAddFragment,
                        null, null, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}