package com.example.protokol.create_protokol;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
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
import android.widget.Toolbar;

import java.util.ArrayList;

public class RoomElementActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    //ЗАГОЛОВКИ
    private String[]header = {"№ п/п", "Месторасположение и наименование электрооборудования", "Кол-во проверенных элементов", "R перех. допустимое, (Ом)", "R перех. измеренное, (Ом)", "Вывод о соответствии нормативному документу"};
    private String date = "Дата проведения проверки «__» ___________ _______г. ";
    private String zag = "Климатические условия при проведении измерений";
    private String uslovia = "Температура воздуха __С. Влажность воздуха __%. Атмосферное давление ___ мм.рт.ст.(бар).";
    private String zag2 = "Нормативные и технические документы, на соответствие требованиям которых проведена проверка:";
    private String line = "______________________________________________________________________________________";

    //ЗАКЛЮЧЕНИЕ
    private String zakl = "Заключение:";
    private String proverka = "Проверку провели:   _____________________     ___________    _____________" + "\n" +
                              "                                           (Должность)                    (Подпись)          (Ф.И.О.)" + "\n" + "\n" +
                              "Проверил:                 _____________________     ___________    _____________" + "\n" +
                              "                                           (Должность)                    (Подпись)          (Ф.И.О.)";
    private TemplatePDF templatePDF;
    private ArrayList<String[]>getNumbers(){
        ArrayList<String[]>rows = new ArrayList<>();
        rows.add(new String[]{"1","2","3","4","5","6"});
        return rows;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_element);

        dbHelper = new DBHelper(this);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        final ListView rooms = findViewById(R.id.rooms);
        Button addRoom = findViewById(R.id.button9);
        Button pdf = findViewById(R.id.button8);

        //НАСТРАИВАЕМ ACTIONBAR
        getSupportActionBar().setSubtitle("Комнаты");
        getSupportActionBar().setTitle("Заземл. уст. и элементы");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА КОМНАТ
        addSpisokRooms(database, rooms);

        //ДОБАВИТЬ КОМНАТУ
        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(RoomElementActivity.this);
                alert.setCancelable(false);
                alert.setTitle("Введите название комнаты:");
                final EditText input = new EditText(RoomElementActivity.this);
                alert.setView(input);
                alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String nameRoom = input.getText().toString();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBHelper.KEY_NAME, nameRoom);
                        database.insert(DBHelper.TABLE_ROOMS, null, contentValues);
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

        //ПЕРЕХОД К ЭЛЕМЕНТАМ И РЕДАКТОР КОМНАТЫ
        rooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, final long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(RoomElementActivity.this);
                alert.setTitle(((TextView) view).getText());
                String arrayMenu[] = {"Перейти к элементам", "Изменить название", "Удалить комнату"};
                alert.setItems(arrayMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //ЗАПРОС В БД ДЛЯ ПОЛУЧЕНИЯ ID НУЖНОЙ КОМНАТЫ
                        Cursor cursor4 = database.query(DBHelper.TABLE_ROOMS, new String[] {DBHelper.KEY_ID}, null, null, null, null, null);
                        cursor4.moveToPosition(position);
                        int idRoomIndex = cursor4.getColumnIndex(DBHelper.KEY_ID);
                        final int idRoom = cursor4.getInt(idRoomIndex);
                        cursor4.close();

                        //ПЕРЕЙТИ К ЭЛЕМЕНТАМ
                        if (which == 0) {
                            Intent intent = new Intent("android.intent.action.RoomElement2");
                            intent.putExtra("nameRoom", ((TextView) view).getText().toString());
                            intent.putExtra("idRoom", idRoom);
                            startActivity(intent);
                        }

                        //ИЗМЕНИТЬ НАЗВАНИЕ
                        if (which == 1) {
                            AlertDialog.Builder alert1 = new AlertDialog.Builder(RoomElementActivity.this);
                            alert1.setCancelable(false);
                            alert1.setTitle("Введите новое название комнаты:");
                            final EditText input = new EditText(RoomElementActivity.this);
                            alert1.setView(input);
                            alert1.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String namer = input.getText().toString();
                                    ContentValues uppname = new ContentValues();
                                    uppname.put(DBHelper.KEY_NAME, namer);
                                    database.update(DBHelper.TABLE_ROOMS,
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
                            AlertDialog.Builder builder4 = new AlertDialog.Builder(RoomElementActivity.this);
                            builder4.setCancelable(false);
                            builder4.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    database.delete(DBHelper.TABLE_ELEMENTS, "room_id = ?", new String[] {String.valueOf(idRoom)});
                                    database.delete(DBHelper.TABLE_ROOMS, "_id = ?", new String[] {String.valueOf(idRoom)});
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

        //ОТКРЫТЬ PDF
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ПРОСТО ОТКРЫТЬ ИЛИ С СОХРАНЕНИЕМ?
                AlertDialog.Builder builder1 = new AlertDialog.Builder(RoomElementActivity.this);
                builder1.setPositiveButton("Посмотреть", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //ПРОСТО ПОСМОТРЕТЬ
                        opPFD(database, null);
                    }
                });
                builder1.setNegativeButton("Сохранить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //ЗАПРАШИВАЕМ НАЗВАНИЕ ФАЙЛА
                        AlertDialog.Builder alert = new AlertDialog.Builder(RoomElementActivity.this);
                        alert.setCancelable(false);
                        alert.setTitle("Введите название сохраняемого файла:");
                        final EditText input = new EditText(RoomElementActivity.this);
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
                Intent intent = new Intent(RoomElementActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //ДОБАВЛЕНИЕ В PDF ЗАГОЛОВКОВ
    public void start (String namefile) {
        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument(namefile, false);
        templatePDF.addMetaData("Protokol", "Item", "Company");
        templatePDF.addTitles_BD("РЕЗУЛЬТАТЫ", "проверки наличия цепи между заземленными установками и элементами заземленной установки", 12);
        templatePDF.addParagraph_NotBD(date, 10);
        templatePDF.addCenter_BD_NotBefore(zag, 12);
        templatePDF.addCenter_NotBD(uslovia, 10);
        templatePDF.addCenter_BD_withBefore(zag2, 12);
        templatePDF.addCenter_NotBD(line, 10);
        templatePDF.createTableRE(header, getNumbers());
    }

    public void addSpisokRooms(SQLiteDatabase database, ListView rooms) {
        final ArrayList<String> spisokRooms = new ArrayList <String>();
        Cursor cursor = database.query(DBHelper.TABLE_ROOMS, new String[] {DBHelper.KEY_NAME}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            do {
                spisokRooms.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spisokRooms);
        rooms.setAdapter(adapter);
    }

    //ОТКРЫТИЕ PDF
    public void opPFD(SQLiteDatabase database, String namefile) {
        ArrayList<String> element = new ArrayList<>();
        ArrayList<ArrayList> elements = new ArrayList<>();
        final ArrayList<String> NZ = new ArrayList<>();
        if (namefile == null)
            namefile = "TepmlatePDF";
        start(namefile);
        String conclusion;
        int roomPrev = -1, r_id, currentElement = 0, currentRoom = 0;
        //ПОЛУЧЕНИЕ ВСЕХ ДАННЫХ ИЗ БД И ЗАПОЛНЕНИЕ ТАБЛИЦЫ
        Cursor cursor = database.rawQuery("select * from rooms as r join elements as e on e.room_id = r._id order by e.room_id", new String[] { });
        if (cursor.moveToFirst()) {
            int roomnameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int elidroomIndex = cursor.getColumnIndex(DBHelper.ROOM_ID);
            int elnameIndex = cursor.getColumnIndex(DBHelper.EL_NAME);
            int elnumberIndex = cursor.getColumnIndex(DBHelper.EL_NUMBER);
            int elsoprIndex = cursor.getColumnIndex(DBHelper.EL_SOPR);
            int conclusionIndex = cursor.getColumnIndex(DBHelper.EL_CONCLUSION);
            do {
                r_id = cursor.getInt(elidroomIndex);
                conclusion = cursor.getString(conclusionIndex);
                //ЕСЛИ ВСТРЕТИЛАСЬ НОВАЯ КОМНАТА
                if ((r_id != roomPrev)){
                    templatePDF.addElementRE(elements);
                    elements = new ArrayList<>();
                    currentElement = 0;
                    currentRoom++;
                    roomPrev = r_id;
                    templatePDF.addRoomRE(cursor.getString(roomnameIndex), String.valueOf(currentRoom) + ". ");
                }
                currentElement++;
                //ПОДСЧИТЫВАЕМ Н.З.
                if (conclusion.equals("не соответствует"))
                    NZ.add(String.valueOf(currentRoom) + "." + String.valueOf(currentElement));
                element.add(String.valueOf(currentElement) + ".");
                element.add(" " + cursor.getString(elnameIndex));
                element.add(cursor.getString(elnumberIndex));
                element.add("0,05");
                element.add(cursor.getString(elsoprIndex));
                element.add(conclusion);
                elements.add(element);
                element = new ArrayList<>();
            } while (cursor.moveToNext());
            templatePDF.addElementRE(elements);
        }
        cursor.close();
        String joinedNZ = TextUtils.join("; ", NZ);
        String end = "a) Проверена целостность и прочность проводников заземления и зануления, переходные контакты их    соединений, болтовые соединения проверены на затяжку, сварные – ударом молотка." + "\n" +
                "b) Сопротивление переходных контактов выше нормы, указаны в п/п ______________." + "\n" +
                "c) Не заземлено оборудование, указанное в п/п : " + joinedNZ + "\n" +
                "d) Величина измеренного переходного сопротивления прочих контактов заземляющих и нулевых проводников,  элементов электрооборудования соответствует (не соответствует) нормам __________________________.";
        templatePDF.addCenter_BD_withBefore(zakl, 12);
        templatePDF.addParagraph_NotBD(end, 10);
        templatePDF.addParagraph_NotBD(proverka, 10);
        templatePDF.closeDocument();
        templatePDF.appViewPDF(RoomElementActivity.this);
    }
}
