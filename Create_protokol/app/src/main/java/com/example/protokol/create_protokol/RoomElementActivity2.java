package com.example.protokol.create_protokol;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Random;

public class RoomElementActivity2 extends AppCompatActivity {

    DBHelper dbHelper;

    private static final String[] elements = new String[]{"Розетка с з.к.", "Розетка без з.к.", "Системный блок", "Сетевой фильтр 5 гн", "Сетевой фильтр 6 гн", "Удлинитель с з.к. 5 гн", "Удлинитель без з.к.", "Принтер", "МФУ", "Блок розеток с з.к. 2 гн", "Копир. аппарат", "СВЧ-печь", "Холодильник"};
    private String[] soprot = {"0,02","0,03","0,04"} ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_element2);

        dbHelper = new DBHelper(this);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        TextView room = findViewById(R.id.textView6);
        final ListView listElements = findViewById(R.id.elements);
        Button addElement = findViewById(R.id.button9);
        final String nameRoom = getIntent().getStringExtra("nameRoom");
        final int idRoom = getIntent().getIntExtra("idRoom", 0);

        //НАСТРАИВАЕМ ACTIONBAR
        getSupportActionBar().setSubtitle("Элементы");
        getSupportActionBar().setTitle("Заземл. уст. и элементы");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ВЫВОД КОМНАТЫ
        room.setText("Комната: " + nameRoom);

        //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА ЩИТОВ
        addSpisokElements(database, listElements, idRoom);

        //ДОБАВИТЬ ЭЛЕМЕНТ
        addElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormAddEl(database, listElements, idRoom);
            }
        });

        //ПОСМОТРЕТЬ, ИЗМЕНИТЬ И УДАЛИТЬ ЭЛЕМЕНТ
        listElements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, final long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(RoomElementActivity2.this);
                alert.setTitle(((TextView) view).getText());
                String arrayMenu[] = {"Посмотреть", "Редактировать", "Удалить элемент"};
                alert.setItems(arrayMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //ЗАПРОС В БД ДЛЯ ПОЛУЧЕНИЯ ID НУЖНОГО ЭЛЕМЕНТА
                        Cursor cursor4 = database.query(DBHelper.TABLE_ELEMENTS, new String[] {DBHelper.EL_ID}, "room_id = ?", new String[] {String.valueOf(idRoom)}, null, null, null);
                        cursor4.moveToPosition(position);
                        int elementIndex = cursor4.getColumnIndex(DBHelper.EL_ID);
                        final int elementId = cursor4.getInt(elementIndex);
                        cursor4.close();

                        //ПОСМОТРЕТЬ
                        if (which == 0) {
                            String numb = "", r = "", conclusion = "";
                            //ПОЛУЧЕНИЕ ИНФОРМАЦИИ ОБ ЭЛЕМЕНТЕ
                            Cursor cursor1 = database.query(DBHelper.TABLE_ELEMENTS, new String[] {DBHelper.EL_ID, DBHelper.EL_NAME,
                                    DBHelper.EL_NUMBER, DBHelper.EL_SOPR, DBHelper.EL_CONCLUSION}, "_id = ?", new String[] {String.valueOf(elementId)}, null, null, null);
                            if (cursor1.moveToFirst()) {
                                int numbIndex = cursor1.getColumnIndex(DBHelper.EL_NUMBER);
                                int rIndex = cursor1.getColumnIndex(DBHelper.EL_SOPR);
                                int conclusionIndex = cursor1.getColumnIndex(DBHelper.EL_CONCLUSION);
                                do {
                                    numb = cursor1.getString(numbIndex);
                                    r = cursor1.getString(rIndex);
                                    conclusion = cursor1.getString(conclusionIndex);
                                } while (cursor1.moveToNext());
                            }
                            cursor1.close();
                            AlertDialog.Builder builder4 = new AlertDialog.Builder(RoomElementActivity2.this);
                            builder4.setCancelable(false);
                            builder4.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                            builder4.setMessage("Кол-во элементов: " + numb + "\n" + "R допустимое: " + "0,05" + "\n" +
                                    "R измеренное: " + r + "\n" + "Вывод: " + conclusion);
                            builder4.setTitle(((TextView) view).getText());
                            AlertDialog dialog4 = builder4.create();
                            dialog4.show();
                        }

                        //ИЗМЕНИТЬ
                        if (which == 1)
                            changeElement(database, listElements, idRoom, elementId);

                        //УДАЛИТЬ ГРУППУ
                        if (which == 2) {

                            //ПОДТВЕРЖДЕНИЕ
                            AlertDialog.Builder builder4 = new AlertDialog.Builder(RoomElementActivity2.this);
                            builder4.setCancelable(false);
                            builder4.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    database.delete(DBHelper.TABLE_ELEMENTS, "_id = ?", new String[] {String.valueOf(elementId)});
                                    //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА ГРУПП
                                    addSpisokElements(database, listElements, idRoom);
                                    Toast toast2 = Toast.makeText(getApplicationContext(),
                                            "Элемент удален", Toast.LENGTH_SHORT);
                                    toast2.show();
                                }
                            });
                            builder4.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                            builder4.setMessage("Вы точно хотите удалить <" + ((TextView) view).getText() + ">?");
                            AlertDialog dialog4 = builder4.create();
                            dialog4.show();
                        }
                    }
                });
                alert.show();
            }
        });
    }

    //НАЗАД
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent("android.intent.action.RoomElement");
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //ИЗМЕНЕНИЕ ЭЛЕМЕНТА С РЕКУРСИЕЙ
    public void changeElement(final SQLiteDatabase database, final ListView listElements, final int idRoom, final int elementId) {
        String el = "", numb = "", r = "";

        //ПОЛУЧЕНИЕ ИНФОРМАЦИИ ОБ ЭЛЕМЕНТЕ
        Cursor cursor1 = database.query(DBHelper.TABLE_ELEMENTS, new String[] {DBHelper.EL_ID, DBHelper.EL_NAME,
                DBHelper.EL_NUMBER, DBHelper.EL_SOPR}, "_id = ?", new String[] {String.valueOf(elementId)}, null, null, null);
        if (cursor1.moveToFirst()) {
            int elIndex = cursor1.getColumnIndex(DBHelper.EL_NAME);
            int numbIndex = cursor1.getColumnIndex(DBHelper.EL_NUMBER);
            int rIndex = cursor1.getColumnIndex(DBHelper.EL_SOPR);
            do {
                el = cursor1.getString(elIndex);
                numb = cursor1.getString(numbIndex);
                r = cursor1.getString(rIndex);
            } while (cursor1.moveToNext());
        }
        cursor1.close();

        final AlertDialog.Builder alert = new AlertDialog.Builder(RoomElementActivity2.this);
        final View myView = getLayoutInflater().inflate(R.layout.dialog_for_add_element,null);
        alert.setCancelable(false);
        alert.setTitle("Редактирование элемента");
        final AutoCompleteTextView nameEl = myView.findViewById(R.id.autoCompleteTextView3);
        ImageView arrow = myView.findViewById(R.id.imageView4);
        final Switch nz = myView.findViewById(R.id.switch8);
        final EditText numbEl = myView.findViewById(R.id.editText3);

        //ЗАПОЛНЕНИЕ ДАННЫХ, КОТОРЫЕ УЖЕ ЕСТЬ
        nameEl.setText(el);
        numbEl.setText(numb);
        if (!r.equals("Н.З."))
            nz.setChecked(false);
        else
            nz.setChecked(true);

        //ОТОБРАЖЕНИЕ ВЫПЛЫВАЮЩЕГО СПИСКА
        ArrayAdapter<String>adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, elements);
        nameEl.setAdapter(adapter1);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(RoomElementActivity2.this);
                nameEl.showDropDown();
            }
        });
        alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String el =  nameEl.getText().toString();
                String numb = numbEl.getText().toString();
                String r, conclusion;
                if (el.equals("") || numb.equals("")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(RoomElementActivity2.this);
                    alert.setCancelable(false);
                    alert.setMessage("Заполните все поля!");
                    alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            changeElement(database, listElements, idRoom, elementId);
                        }
                    });
                    alert.show();
                }
                else {
                    if (!nz.isChecked()) {
                        r = random();
                        conclusion = "соответствует";
                    }
                    else {
                        r = "Н.З.";
                        conclusion = "не соответствует";
                    }
                    //УДАЛЯЕМ СУЩЕСТВУЮЩУЮ СТРОКУ
                    database.delete(DBHelper.TABLE_ELEMENTS, "_id = ?", new String[] {String.valueOf(elementId)});
                    //НА ЕЕ МЕСТЕ ВСТАВЛЯЕМ НОВУЮ
                    ContentValues newEL = new ContentValues();
                    newEL.put(DBHelper.EL_ID, elementId);
                    newEL.put(DBHelper.EL_NAME, el);
                    newEL.put(DBHelper.ROOM_ID, idRoom);
                    newEL.put(DBHelper.EL_NUMBER, numb);
                    newEL.put(DBHelper.EL_SOPR, r);
                    newEL.put(DBHelper.EL_CONCLUSION, conclusion);
                    database.insert(DBHelper.TABLE_ELEMENTS, null, newEL);
                    addSpisokElements(database, listElements, idRoom);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Данные сохранены", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.setView(myView);
        alert.show();
    }

    //ДОБАВЛЕНИЕ ЭЛЕМЕНТА С РЕКУРСИЕЙ
    public void FormAddEl(final SQLiteDatabase database, final ListView listElements, final int idRoom) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(RoomElementActivity2.this);
        final View myView = getLayoutInflater().inflate(R.layout.dialog_for_add_element,null);
        alert.setCancelable(false);
        alert.setTitle("Добавление элемента");
        final AutoCompleteTextView nameEl = myView.findViewById(R.id.autoCompleteTextView3);
        ImageView arrow = myView.findViewById(R.id.imageView4);
        final Switch nz = myView.findViewById(R.id.switch8);
        final EditText numbEl = myView.findViewById(R.id.editText3);
        //ОТОБРАЖЕНИЕ ВЫПЛЫВАЮЩЕГО СПИСКА
        ArrayAdapter<String>adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, elements);
        nameEl.setAdapter(adapter1);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(RoomElementActivity2.this);
                nameEl.showDropDown();
            }
        });
        alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String el =  nameEl.getText().toString();
                String numb = numbEl.getText().toString();
                String r, conclusion;
                if (el.equals("") || numb.equals("")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(RoomElementActivity2.this);
                    alert.setCancelable(false);
                    alert.setMessage("Заполните все поля!");
                    alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            FormAddEl(database, listElements, idRoom);
                        }
                    });
                    alert.show();
                }
                else {
                    if (!nz.isChecked()) {
                        r = random();
                        conclusion = "соответствует";
                    }
                    else {
                        r = "Н.З.";
                        conclusion = "не соответствует";
                    }
                    ContentValues newEL = new ContentValues();
                    newEL.put(DBHelper.EL_NAME, el);
                    newEL.put(DBHelper.ROOM_ID, idRoom);
                    newEL.put(DBHelper.EL_NUMBER, numb);
                    newEL.put(DBHelper.EL_SOPR, r);
                    newEL.put(DBHelper.EL_CONCLUSION, conclusion);
                    database.insert(DBHelper.TABLE_ELEMENTS, null, newEL);
                    //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА ЭЛЕМЕНТОВ
                    addSpisokElements(database, listElements, idRoom);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Элемент <" + el + "> добавлен", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.setView(myView);
        alert.show();
    }

    //ГЕНЕРАТОР
    public String random() {
        Random generator = new Random();
        int randomIndex = generator.nextInt(soprot.length);
        return soprot[randomIndex];
    }

    public void addSpisokElements(SQLiteDatabase database, ListView elements, int idRoom) {
        final ArrayList<String> spisokElements = new ArrayList <String>();
        String r = "";
        Cursor cursor = database.query(DBHelper.TABLE_ELEMENTS, new String[] {DBHelper.EL_NAME, DBHelper.EL_NUMBER, DBHelper.ROOM_ID, DBHelper.EL_SOPR}, "room_id = ?", new String[] {String.valueOf(idRoom)}, null, null, null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.EL_NAME);
            int numberIndex = cursor.getColumnIndex(DBHelper.EL_NUMBER);
            int soprIndex = cursor.getColumnIndex(DBHelper.EL_SOPR);
            do {
                if (cursor.getString(soprIndex).equals("Н.З.")) {
                    r = cursor.getString(soprIndex);
                }
                spisokElements.add(cursor.getString(nameIndex) + " (x" + cursor.getString(numberIndex) + ") " + " " + r);
                r = "";
            } while (cursor.moveToNext());
        }
        cursor.close();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spisokElements);
        elements.setAdapter(adapter);
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
