package com.cuongdang.trackingtask;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class HomeActivity extends AppCompatActivity {

    PieChartView pieChartView;
    SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        pieChartView = findViewById(R.id.chart);
        mDatabase = openOrCreateDatabase(AddTaskActivity.DATABASE_NAME, MODE_PRIVATE, null);
        int numberOfTodoTask = countTaskByStatus("Khởi tạo");
        int numberOfDoingTask = countTaskByStatus("Đang làm");
        int numberOfDoneTask = countTaskByStatus("Hoàn thành");

        List pieData = new ArrayList<>();
        if (numberOfTodoTask != 0) {
            pieData.add(new SliceValue(numberOfTodoTask, Color.GREEN).setLabel("Khởi tạo: " + numberOfTodoTask));
        }
        if (numberOfDoingTask != 0) {
            pieData.add(new SliceValue(numberOfDoingTask, Color.YELLOW).setLabel("Đang làm: " + numberOfDoingTask));
        }
        if (numberOfDoneTask != 0) {
            pieData.add(new SliceValue(numberOfDoneTask, Color.RED).setLabel("Hoàn thành: " + numberOfDoneTask));

        }

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true).setCenterText1("NHIỆM VỤ").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        pieChartView.setPieChartData(pieChartData);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add:
                        Intent a = new Intent(HomeActivity.this, AddTaskActivity.class);
                        startActivity(a);
                        break;
                    case R.id.action_list:
                        Intent b = new Intent(HomeActivity.this, TaskActivity.class);
                        startActivity(b);
                        break;
                }
                return true;
            }
        });
    }

    private int countTaskByStatus(String status) {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM tasks WHERE status like ?",
                new String[]{status});
        int numberOfTask = 0;
        if (cursor.moveToFirst()) {
            do {
                numberOfTask += 1;
            } while (cursor.moveToNext());
        }
        cursor.close();

        return numberOfTask;
    }
}
