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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class InsulationActivity2 extends AppCompatActivity {

    DBHelper dbHelper;
    private int countlines = 0;

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
        setContentView(R.layout.activity_insulation2);

        dbHelper = new DBHelper(this);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        ImageView back = findViewById(R.id.imageView);
        TextView room = findViewById(R.id.textView6);
        final ListView lines = findViewById(R.id.lines);
        Button addLine = findViewById(R.id.button9);
        Button pdf = findViewById(R.id.button8);
        final String nameRoom = getIntent().getStringExtra("nameRoom");
        final long idRoom = getIntent().getLongExtra("idRoom", 0);

        //ВЫВОД КОМНАТЫ
        room.setText(nameRoom);

        //ПОЛУЧЕНИЕ ЗНАЧЕНИЯ СOUNTLINES
        countlines = getCountline(database);

        //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА ЩИТОВ
        addSpisokLines(database, lines, idRoom);

        //НАЗАД
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.Insulation");
                startActivity(intent);
            }
        });


        //ДОБАВИТЬ ЩИТ
        addLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countlines++;
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity2.this);
                alert.setCancelable(false);
                alert.setTitle("Введите название щита:");
                final EditText input = new EditText(InsulationActivity2.this);
                alert.setView(input);
                alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String nameLine = input.getText().toString();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBHelper.LN_ID, countlines);
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
                        countlines = countlines - 1;
                    }
                });
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

                                    //ЗАПРОС В БД И ИЗМЕНЕНИЕ КАЖДОГО ID ЩИТА КАК В ТАБЛИЦЕ ЩИТОВ, ТАК И В ТАБЛИЦЕ ГРУПП НА -1(ПОСЛЕ УДАЛЕННОГО ЭЛЕМЕНТА)
                                    Cursor cursor = database.query(DBHelper.TABLE_LINES, new String[] {DBHelper.LN_ID}, "_id > ?", new String[] {String.valueOf(lineId)}, null, null, null);
                                    if (cursor.moveToFirst()) {
                                        int linechangeIndex = cursor.getColumnIndex(DBHelper.LN_ID);
                                        do {
                                            //В ТАБЛИЦЕ ЩИТОВ
                                            ContentValues uppid = new ContentValues();
                                            uppid.put(DBHelper.LN_ID, String.valueOf(cursor.getInt(linechangeIndex)-1));
                                            database.update(DBHelper.TABLE_LINES,
                                                    uppid,
                                                    "_id = ?",
                                                    new String[] {cursor.getString(linechangeIndex)});

                                            //В ТАБЛИЦЕ ГРУПП
                                            ContentValues uppidInGroup = new ContentValues();
                                            uppidInGroup.put(DBHelper.GR_LINE_ID, String.valueOf(cursor.getInt(linechangeIndex)-1));
                                            database.update(DBHelper.TABLE_GROUPS,
                                                    uppidInGroup,
                                                    "grline_id = ?",
                                                    new String[] {cursor.getString(linechangeIndex)});
                                        } while (cursor.moveToNext());
                                        countlines = countlines - 1;
                                    }
                                    else {
                                        countlines = countlines - 1;
                                    }
                                    cursor.close();
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

        //ОТКРЫТИЕ PDF
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start("HORIZONT");
                templatePDF.closeDocument();
                templatePDF.appViewPDF(InsulationActivity2.this);
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

    int getCountline(SQLiteDatabase database) {
        int numb_lines = 0;
        Cursor cur = database.rawQuery("select count(*) as numb_lines from lines", new String[] { });
        if (cur.moveToFirst()) {
            int numbers_lineidIndex = cur.getColumnIndex("numb_lines");
            do {
                numb_lines = cur.getInt(numbers_lineidIndex);
            } while (cur.moveToNext());
        }
        cur.close();
        return numb_lines;
    }

    public void addSpisokLines(SQLiteDatabase database, ListView rooms, long idRoom) {
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
        rooms.setAdapter(adapter);
    }
}
