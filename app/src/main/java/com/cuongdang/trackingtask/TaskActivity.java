package com.cuongdang.trackingtask;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class TaskActivity extends AppCompatActivity {

    private static int numberOfResult = 0;
    List<Task> taskList;
    SQLiteDatabase mDatabase;
    ListView lvTasks;
    TaskAdapter adapter;
    Button btnSearch;
    EditText edtSearch;
    Spinner spnStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        setControl();
        taskList = new ArrayList<>();
        mDatabase = openOrCreateDatabase(AddTaskActivity.DATABASE_NAME, MODE_PRIVATE, null);
        showTaskFromDatabase();
        setEvent();

    }

    private void setControl() {
        lvTasks = findViewById(R.id.lvTasks);
        btnSearch = findViewById(R.id.btnSearch);
        edtSearch = findViewById(R.id.edtSearch);
        spnStatus = findViewById(R.id.spnStatus);
    }

    private void setEvent() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Intent a = new Intent(TaskActivity.this, HomeActivity.class);
                        startActivity(a);
                        break;
                    case R.id.action_add:
                        Intent b = new Intent(TaskActivity.this, AddTaskActivity.class);
                        startActivity(b);
                        break;
                }
                return true;
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtSearch.getText().toString();
                String status = spnStatus.getSelectedItem().toString();
                searchTaskFromDatabase(name, status);
                Toasty.success(TaskActivity.this, "TÌM KIẾM: " + numberOfResult + " KẾT QUẢ", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showTaskFromDatabase() {
        Cursor cursorTasks = mDatabase.rawQuery("SELECT * FROM tasks", null);
        if (cursorTasks.moveToFirst()) {
            do {
                taskList.add(new Task(
                        cursorTasks.getInt(0),
                        cursorTasks.getString(1),
                        cursorTasks.getString(2),
                        cursorTasks.getString(3),
                        cursorTasks.getString(4)
                ));
            } while (cursorTasks.moveToNext());
        }
        cursorTasks.close();
        adapter = new TaskAdapter(this, R.layout.list_layout_task, taskList, mDatabase);
        lvTasks.setAdapter(adapter);
    }

    private void searchTaskFromDatabase(String name, String status) {
        Cursor cursorTasks = null;
        if (status.equals("Tất cả")) {
            cursorTasks = mDatabase.rawQuery("SELECT * FROM tasks WHERE name like ?",
                    new String[]{"%" + name + "%"});
        } else {
            cursorTasks = mDatabase.rawQuery("SELECT * FROM tasks WHERE name like ? and status like ?",
                    new String[]{"%" + name + "%", status});
        }
        taskList.clear();
        if (cursorTasks.moveToFirst()) {
            do {
                taskList.add(new Task(
                        cursorTasks.getInt(0),
                        cursorTasks.getString(1),
                        cursorTasks.getString(2),
                        cursorTasks.getString(3),
                        cursorTasks.getString(4)
                ));
            } while (cursorTasks.moveToNext());
        }
        cursorTasks.close();
        numberOfResult = taskList.size();
        adapter = new TaskAdapter(this, R.layout.list_layout_task, taskList, mDatabase);
        lvTasks.setAdapter(adapter);
    }
}
