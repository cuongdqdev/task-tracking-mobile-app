package com.cuongdang.trackingtask.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cuongdang.trackingtask.R;
import com.cuongdang.trackingtask.model.Task;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class TaskAdapter extends ArrayAdapter<Task> {

    Context mCtx;
    int listLayoutRes;
    List<Task> taskList;
    SQLiteDatabase mDatabase;

    public TaskAdapter(Context mCtx, int listLayoutRes, List<Task> taskList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, taskList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.taskList = taskList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        // getting employee of the specified position
        final Task task = taskList.get(position);

        //getting views
        TextView tvTaskName = view.findViewById(R.id.tvTaskName);
        TextView tvTaskDescription = view.findViewById(R.id.tvTaskDescription);
        TextView tvStatus = view.findViewById(R.id.tvStatus);
        TextView tvCreatedDate = view.findViewById(R.id.tvCreatedDate);

        // add data to views
        tvTaskName.setText(task.getName());
        tvTaskDescription.setText(task.getDescription());
        tvStatus.setText(task.getStatus());
        if (task.getStatus().equals("Khởi tạo")) {
            tvStatus.setTextColor(Color.GREEN);
        }
        if (task.getStatus().equals("Đang làm")) {
            tvStatus.setTextColor(Color.YELLOW);
        }
        if (task.getStatus().equals("Hoàn thành")) {
            tvStatus.setTextColor(Color.RED);
        }
        tvCreatedDate.setText(task.getCreatedDate());

        Button btnDeleteTask = view.findViewById(R.id.btnDeleteTask);
        Button btnEditTask = view.findViewById(R.id.btnEditTask);

        btnEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTask(task);
            }
        });

        btnDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(mCtx, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Bạn có muốn xóa nhiệm vụ?")
                        .setConfirmText("Xóa!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                String sql = "DELETE FROM tasks WHERE id = ?";
                                mDatabase.execSQL(sql, new Integer[]{task.getId()});
                                Toasty.warning(mCtx, "XÓA NHIỆM VỤ THÀNH CÔNG.", Toast.LENGTH_SHORT, true).show();
                                reloadTasksFromDatabase();
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelButton("Hủy", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });

        return view;
    }

    private void updateTask(final Task task) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.dialog_update_task, null);
        builder.setView(view);


        final EditText edtTaskName = view.findViewById(R.id.edtTaskName);
        final EditText edtTaskDescription = view.findViewById(R.id.edtTaskDescription);
        final Spinner spnStatus = view.findViewById(R.id.spnStatus);

        edtTaskName.setText(task.getName());
        edtTaskDescription.setText(task.getDescription());
        switch (task.getStatus()) {
            case "Đang làm":
                spnStatus.setSelection(1);
                break;
            case "Hoàn thành":
                spnStatus.setSelection(2);
                break;
            default:
                spnStatus.setSelection(0);
                break;
        }

        final AlertDialog dialog = builder.create();
        dialog.show();

        view.findViewById(R.id.btnEditTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskName = edtTaskName.getText().toString().trim();
                String status = spnStatus.getSelectedItem().toString();
                String taskDescription = edtTaskDescription.getText().toString().trim();

                if (taskName.isEmpty()) {
                    edtTaskName.setError("Tên nhiệm vụ không được để trống");
                    edtTaskName.requestFocus();
                    return;
                }

                String sql = "UPDATE tasks \n" +
                        "SET name = ?, \n" +
                        "status = ?, \n" +
                        "description = ? \n" +
                        "WHERE id = ?;\n";

                mDatabase.execSQL(sql, new String[]{taskName, status, taskDescription, String.valueOf(task.getId())});
                Toasty.success(mCtx, "CẬP NHẬT NHIỆM VỤ THÀNH CÔNG", Toast.LENGTH_SHORT).show();
                reloadTasksFromDatabase();

                dialog.dismiss();
            }
        });
    }

    private void reloadTasksFromDatabase() {
        Cursor cursorTasks = mDatabase.rawQuery("SELECT * FROM tasks", null);
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
        notifyDataSetChanged();
    }

}
