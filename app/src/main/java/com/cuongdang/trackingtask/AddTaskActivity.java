package com.cuongdang.trackingtask;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class AddTaskActivity extends AppCompatActivity {

    public static final String DATABASE_NAME = "mytaskdatabase";
    SQLiteDatabase mDatabase;

    EditText edtTaskName, edtTaskDescription;
    Spinner spnStatus;
    Button btnAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        setControl();
        setEvent();

        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        createTaskTable();

    }

    private void setControl() {
        edtTaskName = findViewById(R.id.edtTaskName);
        edtTaskDescription = findViewById(R.id.edtTaskDescription);
        spnStatus = findViewById(R.id.spnStatus);
        btnAddTask = findViewById(R.id.btnAddTask);
    }

    private void setEvent() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Intent home = new Intent(AddTaskActivity.this, HomeActivity.class);
                        startActivity(home);
                        break;
                    case R.id.action_list:
                        Intent task = new Intent(AddTaskActivity.this, TaskActivity.class);
                        startActivity(task);
                        break;
                }
                return true;
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

    }

    private void addTask() {
        String taskName = edtTaskName.getText().toString().trim();
        String taskDescription = edtTaskDescription.getText().toString().trim();
        String status = spnStatus.getSelectedItem().toString();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String createdDate = sdf.format(cal.getTime());

        if (isTaskName(taskName)) {
            String insertSQL = "INSERT INTO tasks \n" +
                    "(name, status, createddate, description)\n" +
                    "VALUES \n" +
                    "(?, ?, ?, ?);";

            mDatabase.execSQL(insertSQL, new String[]{taskName, status, createdDate, taskDescription});
            edtTaskName.setText("");
            edtTaskDescription.setText("");
            Toasty.success(this, "THÊM NHIỆM VỤ THÀNH CÔNG", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isTaskName(String taskName) {
        if (taskName.isEmpty()) {
            edtTaskName.setError("Vui lòng nhập tên nhiệm vụ!");
            edtTaskName.requestFocus();
            return false;
        }
        return true;
    }

    private void createTaskTable() {
        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS tasks (\n" +
                        "    id INTEGER NOT NULL CONSTRAINT tasks_pk PRIMARY KEY AUTOINCREMENT,\n" +
                        "    name varchar(200) NOT NULL,\n" +
                        "    status varchar(200) NOT NULL,\n" +
                        "    createddate datetime NOT NULL,\n" +
                        "    description varchar(200) \n" +
                        ");"
        );
    }
}
