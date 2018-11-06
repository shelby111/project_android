package com.example.protokol.create_protokol;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        Button updatebase = findViewById(R.id.button6);

        //ОБНОВЛЕНИЕ БАЗЫ ДАННЫХ
        updatebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false);
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        database.delete(DBHelper.TABLE_ROOMS, null, null);
                        database.delete(DBHelper.TABLE_ELEMENTS, null, null);
                    }
                });
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setMessage("Вы уверены, что хотите начать работу с новыми данными?");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void roomElement (View view) {
        Intent intent = new Intent("android.intent.action.RoomElement");
        startActivity(intent);
    }
}