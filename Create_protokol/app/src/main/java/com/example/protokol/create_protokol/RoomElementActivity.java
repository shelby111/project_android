package com.example.protokol.create_protokol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class RoomElementActivity extends AppCompatActivity {
    //ЗАГОЛОВКИ
    private String[]header = {"№ п/п", "Месторасположение и наименование электрооборудования", "Кол-во проверенных элементов", "R перех. допустимое, (Ом)", "R перех. измеренное, (Ом)", "Вывод о соответствии нормативному документу"};
    private String date = "Дата проведения проверки «__» ___________ _______г. ";
    private String zag = "Климатические условия при проведении измерений";
    private String uslovia = "Температура воздуха __С. Влажность воздуха __%. Атмосферное давление ___ мм.рт.ст.(бар).";
    private String zag2 = "Нормативные и технические документы, на соответствие требованиям которых проведена проверка:";
    private String line = "______________________________________________________________________________________";
    //ЗАКЛЮЧЕНИЕ
    private String zakl = "Заключение:";
    private String end = "a) Проверена целостность и прочность проводников заземления и зануления, переходные контакты их    соединений, болтовые соединения проверены на затяжку, сварные – ударом молотка." + "\n" +
            "b) Сопротивление переходных контактов выше нормы, указаны в п/п ______________." + "\n" +
            "c) Не заземлено оборудование, указанное в п/п : " +
            "d) Величина измеренного переходного сопротивления прочих контактов заземляющих и нулевых проводников,  элементов электрооборудования соответствует (не соответствует) нормам __________________________.";
    private String proverka = "Проверку провели:   _____________________     ___________    _____________" + "\n" +
                              "                                           (Должность)                    (Подпись)          (Ф.И.О.)" + "\n" + "\n" +
                              "Проверил:                 _____________________     ___________    _____________" + "\n" +
                              "                                           (Должность)                    (Подпись)          (Ф.И.О.)";
    private TemplatePDF templatePDF;
    DBHelper dbHelper;
    private static final String[] elements = new String[]{"Розетка с з.к.", "Розетка без з.к.", "Системный блок", "Сетевой фильтр 5 гн", "Сетевой фильтр 6 гн", "Удлинитель с з.к. 5 гн", "Удлинитель без з.к.", "Принтер", "МФУ", "Блок розеток с з.к. 2 гн", "Копир. аппарат", "СВЧ-печь", "Холодильник"};
    int countroom = 0;
    int roomIdActive;
    private String[] soprot = {"0,02","0,03","0,04"} ;
    private ArrayList<String[]>getClients(){
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

        final AutoCompleteTextView actv2 = findViewById(R.id.actv2);
        final TextView nroom = findViewById(R.id.textView3);
        final Button choice = findViewById(R.id.button5);
        Button room = findViewById(R.id.button);
        Button elem = findViewById(R.id.button2);
        Button openpdf = findViewById(R.id.button3);
        Button editor = findViewById(R.id.button4);
        ImageView image1 = findViewById(R.id.image1);
        final EditText kolvo = findViewById(R.id.kolvo);
        final Switch switch1 = findViewById(R.id.switch1);

        //ПОЛУЧЕНИЕ ЗНАЧЕНИЯ СOUNTROOM
        countroom = getCountroom(database);

        //ОТОБРАЖЕНИЕ ВЫПЛЫВАЮЩЕГО СПИСКА
        ArrayAdapter<String>adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, elements);
        actv2.setAdapter(adapter1);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(RoomElementActivity.this);
                actv2.showDropDown();
            }
        });

        //ВЫБОР КОМНАТЫ
        choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numb_room = 0;
                //ЗАПРОС В БД И ЗАПИСЬ КОМНАТ В СПИСОК
                final ArrayList <String> spisokRooms = new ArrayList <String>();
                Cursor cursor = database.query(DBHelper.TABLE_ROOMS, new String[] {DBHelper.KEY_NAME}, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
                    do {
                        numb_room++;
                        spisokRooms.add(String.valueOf(numb_room) + ". " + cursor.getString(nameIndex));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                if (spisokRooms.isEmpty()) {
                    builder.setMessage("Вы еще не добавляли комнаты");
                }
                builder.setTitle("Выберете комнату:");
                String[] arrayRooms = {};
                arrayRooms = spisokRooms.toArray(new String[spisokRooms.size()]);
                final String[] finalRooms = arrayRooms;
                builder.setItems(arrayRooms, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //ЗАПРОС В БД ДЛЯ ПОЛУЧЕНИЯ КОМНАТЫ
                        Cursor cursor = database.query(DBHelper.TABLE_ROOMS, new String[] {DBHelper.KEY_NAME, DBHelper.KEY_ID}, "_id = ?", new String[] {String.valueOf(which+1)},null, null, null, null);
                        if (cursor.moveToFirst()) {
                            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
                            int ridIndex = cursor. getColumnIndex(DBHelper.KEY_ID);
                            do {
                                roomIdActive = cursor.getInt(ridIndex);
                                nroom.setText(cursor.getString(nameIndex));
                            } while (cursor.moveToNext());
                        }
                        cursor.close();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //ДОБАВЛЕНИЕ КОМНАТЫ
        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countroom++;
                AlertDialog.Builder alert = new AlertDialog.Builder(RoomElementActivity.this);
                alert.setCancelable(false);
                alert.setTitle("Введите название комнаты:");
                final EditText input = new EditText(RoomElementActivity.this);
                alert.setView(input);
                alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String nameRoom = input.getText().toString();

                        //ПОДТВЕРЖДЕНИЕ
                        AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(DBHelper.KEY_ID, countroom);
                                contentValues.put(DBHelper.KEY_NAME, nameRoom);
                                database.insert(DBHelper.TABLE_ROOMS, null, contentValues);
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Комната <" + nameRoom + "> добавлена", Toast.LENGTH_SHORT);
                                toast.show();
                                roomIdActive = countroom;
                                nroom.setText(nameRoom);
                            }
                        });
                        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                countroom = countroom - 1;
                            }
                        });
                        builder.setMessage("Название комнаты: " + nameRoom)
                                .setTitle("Подтвердите данные");
                        AlertDialog dialog1 = builder.create();
                        dialog1.show();

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

        //ДОБАВЛЕНИЕ ЭЛЕМЕНТА
        elem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String choiceRoom = nroom.getText().toString();
                if (choiceRoom.equals("Комната не выбрана")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                    builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.setMessage("Сначала выберете комнату");
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else {
                    final ContentValues newEL = new ContentValues();
                    Boolean switchbool = switch1.isChecked();
                    final String kolvo1 = kolvo.getText().toString();
                    final String r;
                    if (!switchbool) {
                        r = random();
                    } else {
                        r = "Н.З.";
                    }
                    final String nameElement = actv2.getText().toString();
                    hideKeyboard(RoomElementActivity.this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            switch1.setChecked(false);
                            actv2.setText("");
                            kolvo.setText("");
                            newEL.put(DBHelper.ROOM_ID, roomIdActive);
                            newEL.put(DBHelper.EL_NAME, nameElement);
                            newEL.put(DBHelper.EL_NUMBER, kolvo1);
                            newEL.put(DBHelper.EL_SOPR, r);
                            database.insert(DBHelper.TABLE_ELEMENTS, null, newEL);
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Элемент <" + nameElement + "> добавлен", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                    builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.setMessage("Название элемента: " + nameElement + "\n" + "Количество: " + kolvo1 + "\n" + "R(Ом): " + r)
                            .setTitle("Подтвердите данные");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        //РЕДАКТОР
        editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int numb_room = 0;
                //ЗАПРОС В БД И ЗАПИСЬ КОМНАТ В СПИСОК
                ArrayList <String> spisokRooms = new ArrayList <String>();
                Cursor cursor = database.query(DBHelper.TABLE_ROOMS, new String[] {DBHelper.KEY_NAME}, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
                    do {
                        numb_room++;
                        spisokRooms.add(String.valueOf(numb_room) + ". " + cursor.getString(nameIndex));
                    } while (cursor.moveToNext());
                }
                cursor.close();

                //ВЫВОД КОМНАТ
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                if (spisokRooms.isEmpty()) {
                    builder.setMessage("Вы еще не добавляли комнаты");
                }
                builder.setTitle("Выберете комнату:");
                String[] arrayRooms = {};
                arrayRooms = spisokRooms.toArray(new String[spisokRooms.size()]);
                final String[] finalRooms = arrayRooms;
                builder.setItems(arrayRooms, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int r_id = which + 1;
                        int numb_elem = 0;
                        ArrayList <String> spisokElements = new ArrayList <String>();
                        String r = "";
                        //ЗАПРОС В БД И ЗАПИСЬ ЭЛЕМЕНТОВ В СПИСОК
                        Cursor cursor = database.query(DBHelper.TABLE_ELEMENTS, new String[] {DBHelper.EL_NAME, DBHelper.EL_NUMBER, DBHelper.ROOM_ID, DBHelper.EL_SOPR}, "room_id = ?", new String[] {String.valueOf(r_id)}, null, null, null);
                        if (cursor.moveToFirst()) {
                            int nameIndex = cursor.getColumnIndex(DBHelper.EL_NAME);
                            int numberIndex = cursor.getColumnIndex(DBHelper.EL_NUMBER);
                            int soprIndex = cursor.getColumnIndex(DBHelper.EL_SOPR);
                            do {
                                numb_elem++;
                                if (cursor.getString(soprIndex).equals("Н.З.")) {
                                    r = cursor.getString(soprIndex);
                                }
                                spisokElements.add(String.valueOf(numb_elem) + ". " + cursor.getString(nameIndex) + " (x" + cursor.getString(numberIndex) + ") " + " " + r);
                                r = "";
                            } while (cursor.moveToNext());
                        }
                        cursor.close();

                        //ВЫВОД ЭЛЕМЕНТОВ
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(RoomElementActivity.this);
                        if (spisokElements.isEmpty()) {
                            builder2.setMessage("Вы еще не добавляли элементы в этой комнате");
                        }
                        builder2.setTitle(finalRooms[which].substring(3));
                        String[] arrayElements = {};
                        arrayElements = spisokElements.toArray(new String[spisokElements.size()]);
                        final String[] finalElements = arrayElements;
                        builder2.setItems(arrayElements, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final int poz_el = which;

                                //ВЫВОД МЕНЮ: РЕДАКТИРОВАТЬ, УДАЛИТЬ
                                AlertDialog.Builder builder3 = new AlertDialog.Builder(RoomElementActivity.this);
                                builder3.setTitle(finalElements[which]);
                                String arrayMenu[] = {"Редактировать", "Удалить"};
                                builder3.setItems(arrayMenu, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //ЗАПРОС В БД ДЛЯ ПОЛУЧЕНИЯ НУЖНОГО ЭЛЕМЕНТА
                                        Cursor cursor = database.query(DBHelper.TABLE_ELEMENTS, new String[] {DBHelper.EL_ID, DBHelper.EL_NAME, DBHelper.EL_NUMBER, DBHelper.ROOM_ID, DBHelper.EL_SOPR}, "room_id = ?", new String[] {String.valueOf(r_id)}, null, null, null);
                                        cursor.moveToPosition(poz_el);
                                        int elIndex = cursor.getColumnIndex(DBHelper.EL_ID);
                                        int elnameIndex = cursor.getColumnIndex(DBHelper.EL_NAME);
                                        int elnumIndex = cursor.getColumnIndex(DBHelper.EL_NUMBER);
                                        int elrIndex = cursor.getColumnIndex(DBHelper.EL_SOPR);
                                        final int elid = cursor.getInt(elIndex);
                                        final String elr = cursor.getString(elrIndex);
                                        final String elname = cursor.getString(elnameIndex);
                                        final String elnum = cursor.getString(elnumIndex);
                                        cursor.close();

                                        //РЕДАКТИРОВАТЬ
                                        if (which == 0) {
                                            redactor(database, elid, elr);
                                        }

                                        //УДАЛИТЬ
                                        if (which == 1) {

                                            //ПОДТВЕРЖДЕНИЕ
                                            AlertDialog.Builder builder4 = new AlertDialog.Builder(RoomElementActivity.this);
                                            builder4.setCancelable(false);
                                            builder4.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    database.delete(DBHelper.TABLE_ELEMENTS, "_id = ?", new String[] {String.valueOf(elid)});
                                                    Toast toast2 = Toast.makeText(getApplicationContext(),
                                                            "Элемент <" + elname + "> удален", Toast.LENGTH_SHORT);
                                                    toast2.show();
                                                }
                                            });
                                            builder4.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                }
                                            });
                                            builder4.setMessage("Вы точно хотите удалить элемент:\n" + finalElements[poz_el] + "?");
                                            AlertDialog dialog4 = builder4.create();
                                            dialog4.show();

                                        }
                                    }
                                });
                                AlertDialog dialog3 = builder3.create();
                                dialog3.show();

                            }
                        });
                        builder2.setPositiveButton("Удалить комнату", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //ПОДТВЕРЖДЕНИЕ
                                AlertDialog.Builder builder4 = new AlertDialog.Builder(RoomElementActivity.this);
                                builder4.setCancelable(false);
                                builder4.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        if (finalRooms[r_id-1].substring(3).equals(nroom.getText())) {
                                            nroom.setText("Комната не выбрана");
                                        }
                                        database.delete(DBHelper.TABLE_ELEMENTS, "room_id = ?", new String[] {String.valueOf(r_id)});
                                        database.delete(DBHelper.TABLE_ROOMS, "_id = ?", new String[] {String.valueOf(r_id)});

                                        //ЗАПРОС В БД И ИЗМЕНЕНИЕ КАЖДОГО ID КОМНАТЫ КАК В ТАЛИЦЕ ЭЛЕМЕНТОВ, ТАК И В ТАБЛИЦЕ КОМНАТ НА -1(ПОСЛЕ УДАЛЕННОГО ЭЛЕМЕНТА)
                                        Cursor cursor = database.query(DBHelper.TABLE_ROOMS, new String[] {DBHelper.KEY_ID, DBHelper.KEY_NAME}, "_id > ?", new String[] {String.valueOf(r_id)}, null, null, null);
                                        if (cursor.moveToFirst()) {
                                            int roomchangeIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                                            do {
                                                //В ТАБЛИЦЕ КОМНАТ
                                                ContentValues uppid = new ContentValues();
                                                uppid.put(DBHelper.KEY_ID, String.valueOf(cursor.getInt(roomchangeIndex)-1));
                                                database.update(DBHelper.TABLE_ROOMS,
                                                        uppid,
                                                        "_id = ?",
                                                        new String[] {cursor.getString(roomchangeIndex)});

                                                //В ТАБЛИЦЕ ЭЛЕМЕНТОВ
                                                ContentValues uppidInElement = new ContentValues();
                                                uppidInElement.put(DBHelper.ROOM_ID, String.valueOf(cursor.getInt(roomchangeIndex)-1));
                                                database.update(DBHelper.TABLE_ELEMENTS,
                                                        uppidInElement,
                                                        "room_id = ?",
                                                        new String[] {cursor.getString(roomchangeIndex)});
                                            } while (cursor.moveToNext());
                                            countroom = countroom - 1;
                                        }
                                        else {
                                            countroom = countroom - 1;
                                        }
                                        cursor.close();

                                        Toast toast2 = Toast.makeText(getApplicationContext(),
                                                "Комната <" + finalRooms[r_id-1].substring(3) + "> удалена", Toast.LENGTH_SHORT);
                                        toast2.show();

                                    }
                                });
                                builder4.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                                builder4.setMessage("Вы точно хотите удалить комнату <" + finalRooms[r_id-1].substring(3) + ">?");
                                AlertDialog dialog4 = builder4.create();
                                dialog4.show();

                            }
                        });
                        builder2.setNegativeButton("Изменить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                AlertDialog.Builder alert = new AlertDialog.Builder(RoomElementActivity.this);
                                alert.setCancelable(false);
                                alert.setTitle("Введите новое название комнаты:");
                                final EditText input = new EditText(RoomElementActivity.this);
                                alert.setView(input);
                                alert.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        final String namer = input.getText().toString();
                                        ContentValues uppname = new ContentValues();
                                        uppname.put(DBHelper.KEY_NAME, namer);
                                        database.update(DBHelper.TABLE_ROOMS,
                                                uppname,
                                                "_id = ?",
                                                new String[] {String.valueOf(r_id)});
                                        if (r_id == roomIdActive)
                                            nroom.setText(namer);
                                        Toast toast1 = Toast.makeText(getApplicationContext(),
                                                "Название изменено: " + namer, Toast.LENGTH_SHORT);
                                        toast1.show();
                                    }
                                });
                                alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                    }
                                });
                                alert.show();

                            }
                        });
                        AlertDialog dialog2 = builder2.create();
                        dialog2.show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //ОТКРЫТЬ PDF
        openpdf.setOnClickListener(new View.OnClickListener() {
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

    //ДОБАВЛЕНИЕ В PDF ЗАГОЛОВКОВ
    public void start (String namefile) {
        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument(namefile);
        templatePDF.addMetaData("Protokol", "Item", "Company");
        templatePDF.addTitles("РЕЗУЛЬТАТЫ", "проверки наличия цепи между заземленными установками и элементами заземленной установки");
        templatePDF.addParagraph(date);
        templatePDF.addZag(zag);
        templatePDF.addParagraph(uslovia);
        templatePDF.addZag(zag2);
        templatePDF.addParagraph(line);
        templatePDF.createTable(header, getClients());
    }

    //ГЕНЕРАТОР
    public String random() {
        Random generator = new Random();
        int randomIndex = generator.nextInt(soprot.length);
        return soprot[randomIndex];
    }

    //ПОДСЧЕТ КОЛ-ВА ДОБАВЛЕННЫХ КОМНАТ
    int getCountroom(SQLiteDatabase database) {
        int numb_rooms = 0;
        Cursor cur = database.rawQuery("select count(*) as numb_rooms from rooms", new String[] { });
        if (cur.moveToFirst()) {
            int numbers_roomidIndex = cur.getColumnIndex("numb_rooms");
            do {
                numb_rooms = cur.getInt(numbers_roomidIndex);
            } while (cur.moveToNext());
        }
        cur.close();
        return numb_rooms;
    }

    //ОТКРЫТИЕ PDF
    public void opPFD(SQLiteDatabase database, String namefile) {
        if (namefile == null)
            namefile = "TepmlatePDF";
        final ArrayList<String> NZ = new ArrayList<>();
        start(namefile);
        int numb_rooms = getCountroom(database);
        int room_chek = 0, rid, count = 1;
        String res;
        Cursor cursor = database.rawQuery("select * from rooms as r join elements as e on e.room_id = r._id order by e.room_id", new String[] { });
        if (cursor.moveToFirst()) {
            int roomnameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int elidroomIndex = cursor.getColumnIndex(DBHelper.ROOM_ID);
            int elnameIndex = cursor.getColumnIndex(DBHelper.EL_NAME);
            int elnumberIndex = cursor.getColumnIndex(DBHelper.EL_NUMBER);
            int elsoprIndex = cursor.getColumnIndex(DBHelper.EL_SOPR);
            do {
                rid = cursor.getInt(elidroomIndex);
                if ((rid != room_chek) && (rid <= numb_rooms)){
                    count = 0;
                    room_chek++;
                    templatePDF.addRoom(cursor.getString(roomnameIndex), String.valueOf(room_chek) + ". ");
                }
                count++;
                if (cursor.getString(elsoprIndex).equals("Н.З.")){
                    res = "не соответсвует";
                    NZ.add(cursor.getString(elidroomIndex) + "." + String.valueOf(count));
                }
                else
                    res = "cоответсвует";
                String[]element = {String.valueOf(count), cursor.getString(elnameIndex), cursor.getString(elnumberIndex), "0,05", cursor.getString(elsoprIndex), res};
                templatePDF.addElement(element);
            } while (cursor.moveToNext());
        }
        cursor.close();
        String joinedNZ = TextUtils.join("; ", NZ);
        String end = "a) Проверена целостность и прочность проводников заземления и зануления, переходные контакты их    соединений, болтовые соединения проверены на затяжку, сварные – ударом молотка." + "\n" +
                "b) Сопротивление переходных контактов выше нормы, указаны в п/п ______________." + "\n" +
                "c) Не заземлено оборудование, указанное в п/п : " + joinedNZ + "\n" +
                "d) Величина измеренного переходного сопротивления прочих контактов заземляющих и нулевых проводников,  элементов электрооборудования соответствует (не соответствует) нормам __________________________.";
        templatePDF.addCenter(zakl);
        templatePDF.addParagraph(end);
        templatePDF.addParagraph(proverka);
        templatePDF.closeDocument();
        templatePDF.appViewPDF(RoomElementActivity.this);
    }

    //РЕДАКТИРОВАНИЕ С РЕКУРСИЕЙ
    public void redactor(final SQLiteDatabase database, final int elid, final String elr) {
        final Boolean[] exit = {false};
        //ВЫБОР: НАЗВАНИЕ, КОЛИЧЕСВТО, СОПРОТИВЛЕНИЕ
        do {
            AlertDialog.Builder builder5 = new AlertDialog.Builder(RoomElementActivity.this);
            builder5.setCancelable(false);
            builder5.setTitle("Что хотите изменить?");
            builder5.setNegativeButton("Выход из редактирования", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    exit[0] = true;
                }
            });
            String arrayMenu1[] = {"Название", "Количество", "Сопротивление"};
            builder5.setItems(arrayMenu1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //НАЗВАНИЕ
                    if (which == 0) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(RoomElementActivity.this);
                        alert.setCancelable(false);
                        alert.setTitle("Введите новое название элемента:");
                        final EditText input = new EditText(RoomElementActivity.this);
                        alert.setView(input);
                        alert.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final String nameel = input.getText().toString();
                                ContentValues uppname = new ContentValues();
                                uppname.put(DBHelper.EL_NAME, nameel);
                                database.update(DBHelper.TABLE_ELEMENTS,
                                        uppname,
                                        "_id = ?",
                                        new String[] {String.valueOf(elid)});
                                redactor(database, elid, elr);
                                Toast toast1 = Toast.makeText(getApplicationContext(),
                                        "Название изменено: " + nameel, Toast.LENGTH_SHORT);
                                toast1.show();
                            }
                        });
                        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                redactor(database, elid, elr);
                            }
                        });
                        alert.show();
                    }

                    //КОЛИЧЕСТВО
                    if (which == 1) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(RoomElementActivity.this);
                        alert.setCancelable(false);
                        alert.setTitle("Введите количество:");
                        final EditText input = new EditText(RoomElementActivity.this);
                        alert.setView(input);
                        alert.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final String num = input.getText().toString();
                                ContentValues uppnum = new ContentValues();
                                uppnum.put(DBHelper.EL_NUMBER, num);
                                database.update(DBHelper.TABLE_ELEMENTS,
                                        uppnum,
                                        "_id = ?",
                                        new String[] {String.valueOf(elid)});
                                redactor(database, elid, elr);
                                Toast toast1 = Toast.makeText(getApplicationContext(),
                                        "Количество изменено: " + num, Toast.LENGTH_SHORT);
                                toast1.show();
                            }
                        });
                        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                redactor(database, elid, elr);
                            }
                        });
                        alert.show();
                    }

                    //СОПРОТИВЛЕНИЕ
                    if (which == 2) {
                        String elsopr;
                        if (elr.equals("Н.З."))
                            elsopr = random();
                        else
                            elsopr = "Н.З.";
                        ContentValues uppsopr = new ContentValues();
                        uppsopr.put(DBHelper.EL_SOPR, elsopr);
                        database.update(DBHelper.TABLE_ELEMENTS,
                                uppsopr,
                                "_id = ?",
                                new String[] {String.valueOf(elid)});
                        redactor(database, elid, elsopr);
                        Toast toast1 = Toast.makeText(getApplicationContext(),
                                "Сопротивление изменено: " + elsopr, Toast.LENGTH_SHORT);
                        toast1.show();
                    }

                }
            });
            AlertDialog dialog5 = builder5.create();
            dialog5.show();
        } while(exit[0]);
    }

    //СКРЫТИЕ КЛАВИАТУРЫ
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
