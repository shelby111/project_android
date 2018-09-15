package com.example.protokol.create_protokol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    int count = 0;
    int countroom = 0;
    private String[] soprot = {"0,02","0,03","0,04"} ;
    ArrayList<String> NZ = new ArrayList<>();
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
        database.delete(DBHelper.TABLE_ROOMS, null, null);
        database.delete(DBHelper.TABLE_ELEMENTS, null, null);

        final AutoCompleteTextView actv1 = findViewById(R.id.actv1);
        final AutoCompleteTextView actv2 = findViewById(R.id.actv2);
        Button room = findViewById(R.id.button);
        Button elem = findViewById(R.id.button2);
        Button endbtn = findViewById(R.id.button3);
        Button prosmotr = findViewById(R.id.button4);
        ImageView image1 = findViewById(R.id.image1);
        final EditText kolvo = findViewById(R.id.kolvo);
        final Switch switch1 = findViewById(R.id.switch1);

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

        //ДОБАВЛЕНИЕ КОМНАТЫ
        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ContentValues contentValues = new ContentValues();
                countroom++;
                final String nameRoom = actv1.getText().toString();
                final String countroomstr = String.valueOf(countroom) + ". ";
                hideKeyboard(RoomElementActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        count = 0;
                        templatePDF.addRoom(nameRoom, countroomstr);

                        contentValues.put(DBHelper.KEY_ID, countroom);
                        contentValues.put(DBHelper.KEY_NAME, nameRoom);
                        database.insert(DBHelper.TABLE_ROOMS, null, contentValues);

                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Комната <" + nameRoom + "> добавлена", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        countroom = countroom - 1;
                    }
                });
                builder.setMessage("Название комнаты: " + nameRoom)
                        .setTitle("Подтвердите данные");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        //ДОБАВЛЕНИЕ ЭЛЕМЕНТА
        elem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ContentValues newEL = new ContentValues();
                count++;
                Boolean switchbool = switch1.isChecked();
                String countstr = String.valueOf(count) + ".";
                final String kolvo1 = kolvo.getText().toString();
                final String r;
                String res;
                if (!switchbool) {
                    r = random();
                    res = "соответсвует";
                }else {
                    r = "Н.З.";
                    res = "не соответствует";
                }
                final String nameElement = actv2.getText().toString();
                final String[]elements = {countstr, nameElement, kolvo1, "0,05", r, res};
                hideKeyboard(RoomElementActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        templatePDF.addElement(elements);
                        switch1.setChecked(false);
                        actv2.setText("");
                        kolvo.setText("");
                        if (r.equals("Н.З.")) {
                            NZ.add(String.valueOf(countroom) + "." + String.valueOf(count));
                        }

                        newEL.put(DBHelper.EL_NAME, nameElement);
                        newEL.put(DBHelper.EL_NUMBER, kolvo1);
                        newEL.put(DBHelper.ROOM_ID, countroom);
                        database.insert(DBHelper.TABLE_ELEMENTS, null, newEL);

                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Элемент <" + nameElement + "> добавлен", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        count = count - 1;
                    }
                });
                builder.setMessage("Название элемента: " + nameElement + "\n" + "Количество: " + kolvo1 + "\n" + "R(Ом): " + r)
                        .setTitle("Подтвердите данные");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //ПРОСМОТР ДОБАВЛЕННОГО
        prosmotr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList <String> spisokRooms = new ArrayList <String>();
                Cursor cursor = database.query(DBHelper.TABLE_ROOMS, new String[] {DBHelper.KEY_NAME}, null, null, null, null, null);

                if (cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
                    do {
                        spisokRooms.add(cursor.getString(nameIndex));
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
                        ArrayList <String> spisokElements = new ArrayList <String>();
                        Cursor cursor = database.query(DBHelper.TABLE_ELEMENTS, new String[] {DBHelper.EL_NAME, DBHelper.EL_NUMBER, DBHelper.ROOM_ID}, "room_id = ?", new String[] {String.valueOf(which+1)}, null, null, null);
                        if (cursor.moveToFirst()) {
                            int nameIndex = cursor.getColumnIndex(DBHelper.EL_NAME);
                            int numberIndex = cursor.getColumnIndex(DBHelper.EL_NUMBER);
                            do {
                                spisokElements.add(cursor.getString(nameIndex) + " (x" + cursor.getString(numberIndex) + ")");
                            } while (cursor.moveToNext());
                        }
                        cursor.close();

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(RoomElementActivity.this);
                        if (spisokElements.isEmpty()) {
                            builder2.setMessage("Вы еще не добавляли элементы в этой комнате");
                        }
                        builder2.setTitle(finalRooms[which]);
                        String[] arrayElements = {};
                        arrayElements = spisokElements.toArray(new String[spisokElements.size()]);
                        final String[] finalElements = arrayElements;
                        builder2.setItems(arrayElements, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
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

        //ДОБАВИТЬ КОНЦОВКУ И ОТКРЫТЬ PDF
        endbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setMessage("Вы точно хотите завершить работу?");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        start();
    }

    //ДОБАВЛЕНИЕ В PDF ЗАГОЛОВКОВ
    public void start () {
        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument();
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
