package com.example.protokol.create_protokol;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class InsulationActivity extends AppCompatActivity {

    DBHelper dbHelper;
    private int countroom = 0;

    private String date = "Дата проведения проверки «__» ___________ _______г. ";
    private String zag = "Климатические условия при проведении измерений";
    private String uslovia = "Температура воздуха __С. Влажность воздуха __%. Атмосферное давление ___ мм.рт.ст.(бар).";
    private String zag2 = "Нормативные и технические документы, на соответствие требованиям которых проведена проверка:";
    private String line = "______________________________________________________________________________________________________________";
    private String[] header = {"№\nп/п", "Наименование линий, по проекту", "Рабочее\nнапряжение, В", "Марка провода,\nкабеля", "Количество\nжил, сечение\nпровода,\nкабеля, мм кв.",
            "Напряжение\nмегаомметра, В", "Допустимое\nсопротивление\nизоляции, МОм", "Сопротивление изоляции, МОм", "L1-L2\n(A-B)", "L2-L3\n(В-С)", "L3-L1\n(C-A)", "L1-N\n(A-N)\n(PEN)",
            "L2-N\n(B-N)\n(PEN)", "L3-N\n(C-N)\n(PEN)", "L1-PE\n(A-PE)", "L2-PE\n(B-PE)", "L3-PE\n(C-PE)", "N-PE", "Вывод о\nсоответствии\nнормативному\nдокументу"};
    private TemplatePDF templatePDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insulation);

        dbHelper = new DBHelper(this);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        final ListView rooms = findViewById(R.id.rooms);
        Button addRoom = findViewById(R.id.button9);
        Button pdf = findViewById(R.id.button8);

        //ПОЛУЧЕНИЕ ЗНАЧЕНИЯ СOUNTROOM
        countroom = getCountroom(database);

        //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА КОМНАТ
        addSpisokRooms(database, rooms);

        //ДОБАВИТЬ КОМНАТУ
        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countroom++;
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity.this);
                alert.setCancelable(false);
                alert.setTitle("Введите название комнаты:");
                final EditText input = new EditText(InsulationActivity.this);
                alert.setView(input);
                alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String nameRoom = input.getText().toString();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBHelper.LNR_ID, countroom);
                        contentValues.put(DBHelper.LNR_NAME, nameRoom);
                        database.insert(DBHelper.TABLE_LINE_ROOMS, null, contentValues);
                        //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА КОМНАТ
                        addSpisokRooms(database, rooms);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Комната <" + nameRoom + "> добавлена", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        countroom = countroom - 1;
                    }
                });
                alert.show();
            }
        });

        //ПЕРЕХОД К ЩИТАМ И РЕДАКТОР КОМНАТЫ
        rooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, final long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity.this);
                alert.setTitle(((TextView) view).getText());
                String arrayMenu[] = {"Перейти к щитам", "Изменить название", "Удалить комнату"};
                alert.setItems(arrayMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //ПЕРЕЙТИ К ЩИТАМ
                        if (which == 0) {
                            Intent intent = new Intent("android.intent.action.Insulation2");
                            intent.putExtra("nameRoom", ((TextView) view).getText().toString());
                            intent.putExtra("idRoom", id + 1);
                            startActivity(intent);
                        }

                        //ИЗМЕНИТЬ НАЗВАНИЕ
                        if (which == 1) {
                            AlertDialog.Builder alert1 = new AlertDialog.Builder(InsulationActivity.this);
                            alert1.setCancelable(false);
                            alert1.setTitle("Введите новое название комнаты:");
                            final EditText input = new EditText(InsulationActivity.this);
                            alert1.setView(input);
                            alert1.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String namer = input.getText().toString();
                                    ContentValues uppname = new ContentValues();
                                    uppname.put(DBHelper.LNR_NAME, namer);
                                    database.update(DBHelper.TABLE_LINE_ROOMS,
                                            uppname,
                                            "_id = ?",
                                            new String[] {String.valueOf(id + 1)});
                                    //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА КОМНАТ
                                    addSpisokRooms(database, rooms);
                                    Toast toast1 = Toast.makeText(getApplicationContext(),
                                            "Название изменено: " + namer, Toast.LENGTH_SHORT);
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
                            AlertDialog.Builder builder4 = new AlertDialog.Builder(InsulationActivity.this);
                            builder4.setCancelable(false);
                            builder4.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    database.delete(DBHelper.TABLE_GROUPS, "grlnr_id = ?", new String[] {String.valueOf(id + 1)});
                                    database.delete(DBHelper.TABLE_LINES, "lnr_id = ?", new String[] {String.valueOf(id + 1)});
                                    database.delete(DBHelper.TABLE_LINE_ROOMS, "_id = ?", new String[] {String.valueOf(id + 1)});

                                    //ЗАПРОС В БД И ИЗМЕНЕНИЕ КАЖДОГО ID КОМНАТЫ КАК В ТАБЛИЦЕ ГРУПП, ЛИНИЙ, ТАК И В ТАБЛИЦЕ КОМНАТ НА -1(ПОСЛЕ УДАЛЕННОГО ЭЛЕМЕНТА)
                                    Cursor cursor = database.query(DBHelper.TABLE_LINE_ROOMS, new String[] {DBHelper.LNR_ID, DBHelper.LNR_NAME}, "_id > ?", new String[] {String.valueOf(id + 1)}, null, null, null);
                                    if (cursor.moveToFirst()) {
                                        int roomchangeIndex = cursor.getColumnIndex(DBHelper.LNR_ID);
                                        do {
                                            //В ТАБЛИЦЕ КОМНАТ
                                            ContentValues uppid = new ContentValues();
                                            uppid.put(DBHelper.LNR_ID, String.valueOf(cursor.getInt(roomchangeIndex)-1));
                                            database.update(DBHelper.TABLE_LINE_ROOMS,
                                                    uppid,
                                                    "_id = ?",
                                                    new String[] {cursor.getString(roomchangeIndex)});

                                            //В ТАБЛИЦЕ ЛИНИЙ
                                            ContentValues uppidInLine = new ContentValues();
                                            uppidInLine.put(DBHelper.LN_ID_ROOM, String.valueOf(cursor.getInt(roomchangeIndex)-1));
                                            database.update(DBHelper.TABLE_LINES,
                                                    uppidInLine,
                                                    "lnr_id = ?",
                                                    new String[] {cursor.getString(roomchangeIndex)});

                                            //В ТАБЛИЦЕ ГРУПП
                                            ContentValues uppidInGroup = new ContentValues();
                                            uppidInGroup.put(DBHelper.GR_LNR_ID, String.valueOf(cursor.getInt(roomchangeIndex)-1));
                                            database.update(DBHelper.TABLE_GROUPS,
                                                    uppidInGroup,
                                                    "grlnr_id = ?",
                                                    new String[] {cursor.getString(roomchangeIndex)});
                                        } while (cursor.moveToNext());
                                        countroom = countroom - 1;
                                    }
                                    else {
                                        countroom = countroom - 1;
                                    }
                                    cursor.close();
                                    //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА КОМНАТ
                                    addSpisokRooms(database, rooms);
                                    Toast toast2 = Toast.makeText(getApplicationContext(),
                                            "Комната <" + ((TextView) view).getText() + "> удалена", Toast.LENGTH_SHORT);
                                    toast2.show();

                                }
                            });
                            builder4.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                            builder4.setMessage("Вы точно хотите удалить комнату <" + ((TextView) view).getText() + ">?");
                            AlertDialog dialog4 = builder4.create();
                            dialog4.show();
                        }
                    }
                });
                alert.show();
            }
        });

        //ОТКРЫТИЕ PDF
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start("HORIZONT");
                templatePDF.closeDocument();
                templatePDF.appViewPDF(InsulationActivity.this);
            }
        });
    }

    public void start(String namefile) {
        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument(namefile, true);
        templatePDF.addMetaData("Protokol", "Item", "Company");
        templatePDF.addTitles("РЕЗУЛЬТАТЫ", "проверки сопротивления изоляции проводов и кабелей");
        templatePDF.addParagraph(date);
        templatePDF.addZag(zag);
        templatePDF.addCenterNotBD(uslovia);
        templatePDF.addCenter(zag2);
        templatePDF.addCenterNotBD(line);
        templatePDF.createTableInsulation(header);
    }

    int getCountroom(SQLiteDatabase database) {
        int numb_rooms = 0;
        Cursor cur = database.rawQuery("select count(*) as numb_rooms from lnrooms", new String[] { });
        if (cur.moveToFirst()) {
            int numbers_roomidIndex = cur.getColumnIndex("numb_rooms");
            do {
                numb_rooms = cur.getInt(numbers_roomidIndex);
            } while (cur.moveToNext());
        }
        cur.close();
        return numb_rooms;
    }

    public void addSpisokRooms(SQLiteDatabase database, ListView rooms) {
        final ArrayList<String> spisokRooms = new ArrayList <String>();
        Cursor cursor = database.query(DBHelper.TABLE_LINE_ROOMS, new String[] {DBHelper.LNR_NAME}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.LNR_NAME);
            do {
                spisokRooms.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spisokRooms);
        rooms.setAdapter(adapter);
    }
}
