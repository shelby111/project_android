package com.example.protokol.create_protokol;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class InsulationActivity extends AppCompatActivity {

    DBHelper dbHelper;

    private String date = "Дата проведения проверки «__» ___________ _______г. ";
    private String zag = "Климатические условия при проведении измерений";
    private String uslovia = "Температура воздуха __С. Влажность воздуха __%. Атмосферное давление ___ мм.рт.ст.(бар).";
    private String zag2 = "Нормативные и технические документы, на соответствие требованиям которых проведена проверка:";
    private String line = "_______________________________________________________________________________________________________________________";
    private String proverka = "               Проверку провели:                  _____________________                    ___________                   _____________" + "\n" +
            "                                                                         (Должность)                                   (Подпись)                         (Ф.И.О.)" + "\n" + "\n" +
            "               Проверил:                                _____________________                    ___________                   _____________" + "\n" +
            "                                                                         (Должность)                                   (Подпись)                         (Ф.И.О.)";
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

        //НАСТРАИВАЕМ ACTIONBAR
        getSupportActionBar().setSubtitle("Комнаты");
        getSupportActionBar().setTitle("Изоляция");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА КОМНАТ
        addSpisokRooms(database, rooms);

        //ДОБАВИТЬ КОМНАТУ
        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity.this);
                alert.setCancelable(false);
                alert.setTitle("Введите название комнаты:");
                final EditText input = new EditText(InsulationActivity.this);
                alert.setView(input);
                alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String nameRoom = input.getText().toString();
                        ContentValues contentValues = new ContentValues();
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

                    }
                });
                alert.show();
            }
        });

        //ПЕРЕХОД К ЩИТАМ И РЕДАКТОР КОМНАТЫ
        rooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, final long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity.this);
                alert.setTitle(((TextView) view).getText());
                String arrayMenu[] = {"Перейти к щитам", "Изменить название", "Удалить комнату"};
                alert.setItems(arrayMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //ЗАПРОС В БД ДЛЯ ПОЛУЧЕНИЯ ID НУЖНОЙ КОМНАТЫ
                        Cursor cursor4 = database.query(DBHelper.TABLE_LINE_ROOMS, new String[] {DBHelper.LNR_ID}, null, null, null, null, null);
                        cursor4.moveToPosition(position);
                        int idRoomIndex = cursor4.getColumnIndex(DBHelper.LNR_ID);
                        final int idRoom = cursor4.getInt(idRoomIndex);
                        cursor4.close();

                        //ПЕРЕЙТИ К ЩИТАМ
                        if (which == 0) {
                            Intent intent = new Intent("android.intent.action.Insulation2");
                            intent.putExtra("nameRoom", ((TextView) view).getText().toString());
                            intent.putExtra("idRoom", idRoom);
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
                                            new String[] {String.valueOf(idRoom)});
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
                                    database.delete(DBHelper.TABLE_GROUPS, "grlnr_id = ?", new String[] {String.valueOf(idRoom)});
                                    database.delete(DBHelper.TABLE_LINES, "lnr_id = ?", new String[] {String.valueOf(idRoom)});
                                    database.delete(DBHelper.TABLE_LINE_ROOMS, "_id = ?", new String[] {String.valueOf(idRoom)});
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
                //ПРОСТО ОТКРЫТЬ ИЛИ С СОХРАНЕНИЕМ?
                AlertDialog.Builder builder1 = new AlertDialog.Builder(InsulationActivity.this);
                builder1.setPositiveButton("Посмотреть", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //ПРОСТО ПОСМОТРЕТЬ
                        opPFD(database, null);
                    }
                });
                builder1.setNegativeButton("Сохранить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //ЗАПРАШИВАЕМ НАЗВАНИЕ ФАЙЛА
                        AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity.this);
                        alert.setCancelable(false);
                        alert.setTitle("Введите название сохраняемого файла:");
                        final EditText input = new EditText(InsulationActivity.this);
                        alert.setView(input);
                        alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String namefile = input.getText().toString();
                                if (namefile.equals(""))
                                    namefile = null;
                                opPFD(database, namefile);
                            }
                        });
                        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                        alert.show();
                    }
                });
                builder1.setMessage("Хотите просто посмотреть файл или же открыть с дальнейшим сохранением?");
                AlertDialog dialog1 = builder1.create();
                dialog1.show();
            }
        });
    }

    //НАЗАД
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(InsulationActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void start(String namefile) {
        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument(namefile, true);
        templatePDF.addMetaData("Protokol", "Item", "Company");
        templatePDF.addTitles_BD("РЕЗУЛЬТАТЫ", "проверки сопротивления изоляции проводов и кабелей", 12);
        templatePDF.addParagraph_NotBD(date, 10);
        templatePDF.addCenter_BD_NotBefore(zag, 12);
        templatePDF.addCenter_NotBD(uslovia, 10);
        templatePDF.addCenter_BD_withBefore(zag2, 12);
        templatePDF.addCenter_NotBD(line, 10);
        templatePDF.createTableInsulation(header);
    }

    //ОТКРЫТИЕ PDF
    public void opPFD(SQLiteDatabase database, String namefile) {
        ArrayList<String> group = new ArrayList<>();
        ArrayList<ArrayList> groups = new ArrayList<>();
        if (namefile == null)
            namefile = "TepmlatePDF";
        start(namefile);
        int roomPrev = 0, linePrev = 0, currentLine = 0, currentRoom = 0;
        Cursor cursor = database.rawQuery("select r._id as r_id, l._id as l_id, room, line, lnr_id, grline_id, name_group, u1, " +
                "mark, vein, section, u2, r, a_b, b_c, c_a, a_n, b_n, c_n, a_pe, b_pe, c_pe, n_pe, " +
                "conclusion from lnrooms as r join `lines` as l on l.lnr_id = r._id join groups as g on g.grline_id = l._id order by r._id, l._id", new String[] { });
        if (cursor.moveToFirst()) {
            int r_idIndex = cursor.getColumnIndex("r_id");
            int nameRoomIndex = cursor.getColumnIndex(DBHelper.LNR_NAME);
            int l_idIndex = cursor.getColumnIndex("l_id");
            int nameLineIndex = cursor.getColumnIndex(DBHelper.LN_NAME);
            int nameGroupIndex = cursor.getColumnIndex(DBHelper.GR_NAME);
            int markIndex = cursor.getColumnIndex(DBHelper.GR_MARK);
            int veinIndex = cursor. getColumnIndex(DBHelper.GR_VEIN);
            int sectionIndex = cursor. getColumnIndex(DBHelper.GR_SECTION);
            int workUIndex = cursor. getColumnIndex(DBHelper.GR_U1);
            int uIndex = cursor. getColumnIndex(DBHelper.GR_U2);
            int rIndex = cursor. getColumnIndex(DBHelper.GR_R);
            int a_bIndex = cursor. getColumnIndex(DBHelper.GR_A_B);
            int b_cIndex = cursor. getColumnIndex(DBHelper.GR_B_C);
            int c_aIndex = cursor. getColumnIndex(DBHelper.GR_C_A);
            int a_nIndex = cursor. getColumnIndex(DBHelper.GR_A_N);
            int b_nIndex = cursor. getColumnIndex(DBHelper.GR_B_N);
            int c_nIndex = cursor. getColumnIndex(DBHelper.GR_C_N);
            int a_peIndex = cursor. getColumnIndex(DBHelper.GR_A_PE);
            int b_peIndex = cursor. getColumnIndex(DBHelper.GR_B_PE);
            int c_peIndex = cursor. getColumnIndex(DBHelper.GR_C_PE);
            int n_peIndex = cursor. getColumnIndex(DBHelper.GR_N_PE);
            int conclusionIndex = cursor. getColumnIndex(DBHelper.GR_CONCLUSION);
            do {
                int r_id = cursor.getInt(r_idIndex);
                int l_id = cursor.getInt(l_idIndex);
                //ЕСЛИ ВСТРЕТИЛАСЬ НОВАЯ КОМНАТА
                if ((r_id != roomPrev)){
                    templatePDF.addGroupsInsulation(groups);
                    groups = new ArrayList<>();
                    currentLine = 0;
                    currentRoom++;
                    roomPrev = r_id;
                    templatePDF.addRoomInsulation(String.valueOf(currentRoom) + ". " + cursor.getString(nameRoomIndex));
                }
                //ЕСЛИ ВСТРЕТИЛСЯ НОВЫЙ ЩИТ
                if ((l_id != linePrev)){
                    templatePDF.addGroupsInsulation(groups);
                    groups = new ArrayList<>();
                    linePrev = l_id;
                    currentLine++;
                    templatePDF.addLineInsulation(String.valueOf(currentLine) + ". ", " " + cursor.getString(nameLineIndex));
                }
                String vein = cursor.getString(veinIndex);
                group.add(cursor.getString(nameGroupIndex));
                group.add(cursor.getString(workUIndex));
                group.add(cursor.getString(markIndex));
                if (vein.equals("-"))
                    group.add("-");
                else
                    group.add(vein + "x" + cursor.getString(sectionIndex));
                group.add(cursor.getString(uIndex));
                group.add(cursor.getString(rIndex));
                group.add(cursor.getString(a_bIndex));
                group.add(cursor.getString(b_cIndex));
                group.add(cursor.getString(c_aIndex));
                group.add(cursor.getString(a_nIndex));
                group.add(cursor.getString(b_nIndex));
                group.add(cursor.getString(c_nIndex));
                group.add(cursor.getString(a_peIndex));
                group.add(cursor.getString(b_peIndex));
                group.add(cursor.getString(c_peIndex));
                group.add(cursor.getString(n_peIndex));
                group.add(cursor.getString(conclusionIndex));
                groups.add(group);
                group = new ArrayList<>();
            } while (cursor.moveToNext());
            templatePDF.addGroupsInsulation(groups);
        }
        cursor.close();
        String end = "Примечание: ___________________________________________________________________________________________________________________";
        String end2 = "Заключене: ____________________________________________________________________________________________________________________" + "\n" +
                "_______________________________________________________________________________________________________________________________";
        templatePDF.addParagraph_BD(end, 12);
        templatePDF.addParagraph_BD(end2, 12);
        templatePDF.addParagraph_NotBD(proverka, 10);
        templatePDF.closeDocument();
        templatePDF.appViewPDF(InsulationActivity.this);
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
