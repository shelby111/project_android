package com.example.protokol.create_protokol;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class InsulationActivity2 extends AppCompatActivity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insulation2);

        dbHelper = new DBHelper(this);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        TextView room = findViewById(R.id.textView6);
        final ListView lines = findViewById(R.id.lines);
        Button addLine = findViewById(R.id.button9);
        final String nameRoom = getIntent().getStringExtra("nameRoom");
        final int idRoom = getIntent().getIntExtra("idRoom", 0);

        //НАСТРАИВАЕМ ACTIONBAR
        getSupportActionBar().setSubtitle("Щиты");
        getSupportActionBar().setTitle("Изоляция");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ВЫВОД КОМНАТЫ
        room.setText("Комната: " + nameRoom);

        //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА ЩИТОВ
        addSpisokLines(database, lines, idRoom);

        //ДОБАВИТЬ ЩИТ
        addLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity2.this);
                View myView = getLayoutInflater().inflate(R.layout.dialog_for_names,null);
                alert.setCancelable(false);
                alert.setTitle("Введите название щита:");
                final EditText input = myView.findViewById(R.id.editText);
                alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String nameLine = input.getText().toString();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBHelper.LN_NAME, nameLine);
                        contentValues.put(DBHelper.LN_ID_ROOM, idRoom);
                        database.insert(DBHelper.TABLE_LINES, null, contentValues);
                        //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА ЩИТОВ
                        addSpisokLines(database, lines, idRoom);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Щит <" + nameLine + "> добавлен", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.setView(myView);
                alert.show();
            }
        });

        //ПЕРЕХОД К ГРУППАМ И РЕДАКТОР ЩИТА
        lines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, final long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity2.this);
                alert.setTitle(((TextView) view).getText());
                String arrayMenu[] = {"Перейти к группам", "Изменить название", "Удалить щит"};
                alert.setItems(arrayMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //ЗАПРОС В БД ДЛЯ ПОЛУЧЕНИЯ НУЖНОЙ ЛИНИИ
                        Cursor cursor = database.query(DBHelper.TABLE_LINES, new String[] {DBHelper.LN_ID}, "lnr_id = ?", new String[] {String.valueOf(idRoom)}, null, null, null);
                        cursor.moveToPosition(position);
                        int lineIndex = cursor.getColumnIndex(DBHelper.LN_ID);
                        final int lineId = cursor.getInt(lineIndex);
                        cursor.close();

                        //ПЕРЕЙТИ К ГРУППАМ
                        if (which == 0) {
                            Intent intent = new Intent("android.intent.action.Insulation3");
                            intent.putExtra("nameRoom", nameRoom);
                            intent.putExtra("idRoom", idRoom);
                            intent.putExtra("nameLine", ((TextView) view).getText().toString());
                            intent.putExtra("idLine", lineId);
                            startActivity(intent);
                        }

                        //ИЗМЕНИТЬ НАЗВАНИЕ
                        if (which == 1) {
                            AlertDialog.Builder alert1 = new AlertDialog.Builder(InsulationActivity2.this);
                            alert1.setCancelable(false);
                            alert1.setTitle("Введите новое название щита:");
                            final EditText input = new EditText(InsulationActivity2.this);
                            alert1.setView(input);
                            alert1.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String namel = input.getText().toString();
                                    ContentValues uppname = new ContentValues();
                                    uppname.put(DBHelper.LN_NAME, namel);
                                    database.update(DBHelper.TABLE_LINES,
                                            uppname,
                                            "_id = ?",
                                            new String[] {String.valueOf(lineId)});
                                    //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА ЩИТОВ
                                    addSpisokLines(database, lines, idRoom);
                                    Toast toast1 = Toast.makeText(getApplicationContext(),
                                            "Название изменено: " + namel, Toast.LENGTH_SHORT);
                                    toast1.show();
                                }
                            });
                            alert1.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                            alert1.show();
                        }

                        //УДАЛИТЬ КОМНАТУ
                        if (which == 2) {

                            //ПОДТВЕРЖДЕНИЕ
                            AlertDialog.Builder builder4 = new AlertDialog.Builder(InsulationActivity2.this);
                            builder4.setCancelable(false);
                            builder4.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    database.delete(DBHelper.TABLE_GROUPS, "grline_id = ?", new String[] {String.valueOf(lineId)});
                                    database.delete(DBHelper.TABLE_LINES, "_id = ?", new String[] {String.valueOf(lineId)});
                                    //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА ЩИТОВ
                                    addSpisokLines(database, lines, idRoom);
                                    Toast toast2 = Toast.makeText(getApplicationContext(),
                                            "Щит <" + ((TextView) view).getText() + "> удален", Toast.LENGTH_SHORT);
                                    toast2.show();
                                }
                            });
                            builder4.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                            builder4.setMessage("Вы точно хотите удалить щит <" + ((TextView) view).getText() + ">?");
                            AlertDialog dialog4 = builder4.create();
                            dialog4.show();
                        }
                    }
                });
                alert.show();
            }
        });
    }

    //НА ГЛАВНУЮ
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    //НАЗАД
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent("android.intent.action.Insulation");
                startActivity(intent);
                return true;
            case R.id.action_main:
                Intent intent1 = new Intent(InsulationActivity2.this, MainActivity.class);
                startActivity(intent1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addSpisokLines(SQLiteDatabase database, ListView lines, int idRoom) {
        final ArrayList<String> spisokLines = new ArrayList <String>();
        Cursor cursor = database.query(DBHelper.TABLE_LINES, new String[] {DBHelper.LN_NAME}, "lnr_id = ?", new String[] {String.valueOf(idRoom)}, null, null, null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.LN_NAME);
            do {
                spisokLines.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spisokLines);
        lines.setAdapter(adapter);
    }
}
