package com.cuongdang.trackingtask.activity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cuongdang.trackingtask.R;
import com.cuongdang.trackingtask.broadcast.ReminderBroadcast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class HomeActivity extends AppCompatActivity {

    public static int todo = 0;
    public static int doing = 0;
    public static int done = 0;
    PieChartView pieChartView;
    SQLiteDatabase mDatabase;
    Button btnNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        pieChartView = findViewById(R.id.chart);
        mDatabase = openOrCreateDatabase(AddTaskActivity.DATABASE_NAME, MODE_PRIVATE, null);
        todo = countTaskByStatus("Khởi tạo");
        doing = countTaskByStatus("Đang làm");
        done = countTaskByStatus("Hoàn thành");

        List pieData = new ArrayList<>();
        if (todo != 0) {
            pieData.add(new SliceValue(todo, Color.GREEN).setLabel("Khởi tạo: " + todo));
        }
        if (doing != 0) {
            pieData.add(new SliceValue(doing, Color.YELLOW).setLabel("Đang làm: " + doing));
        }
        if (done != 0) {
            pieData.add(new SliceValue(done, Color.RED).setLabel("Hoàn thành: " + done));
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
        createNotificationChannel();
        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.warning(getApplicationContext(), "THIẾT LẬP NHẮC NHỞ SAU 3 GIỜ", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(HomeActivity.this, ReminderBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0, intent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                long timeAtButtonClick = System.currentTimeMillis();

                //long threeHoursInMillis = 1000 * 60 * 60 * 3;
                long tenSecondsInMillis = 1000 * 10;

                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        timeAtButtonClick + tenSecondsInMillis,
                        pendingIntent);
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder Channel";
            String description = "Channel for Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyId", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
