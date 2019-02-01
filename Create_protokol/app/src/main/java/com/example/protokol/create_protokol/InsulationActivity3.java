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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class InsulationActivity3 extends AppCompatActivity {

    DBHelper dbHelper;
    String nameRoom;
    int idRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insulation3);

        dbHelper = new DBHelper(this);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        TextView room = findViewById(R.id.textView6);
        TextView line = findViewById(R.id.textView7);
        final ListView groups = findViewById(R.id.groups);
        Button addGroup = findViewById(R.id.button9);
        nameRoom = getIntent().getStringExtra("nameRoom");
        idRoom = getIntent().getIntExtra("idRoom", 0);
        final String nameLine = getIntent().getStringExtra("nameLine");
        final int idLine = getIntent().getIntExtra("idLine", 0);

        //НАСТРАИВАЕМ ACTIONBAR
        getSupportActionBar().setSubtitle("Группы");
        getSupportActionBar().setTitle("Изоляция");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ВЫВОД КОМНАТЫ И ЩИТА
        room.setText("Комната: " + nameRoom);
        line.setText("Щит: " + nameLine);

        //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА ГРУПП
        addSpisokGroups(database, groups, idLine);

        //ДОБАВИТЬ ГРУППУ
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.Insulation4");
                intent.putExtra("nameRoom", nameRoom);
                intent.putExtra("idRoom", idRoom);
                intent.putExtra("nameLine", nameLine);
                intent.putExtra("idLine", idLine);
                intent.putExtra("nameGroup", "Группа №" + String.valueOf(groups.getAdapter().getCount() + 1));
                startActivity(intent);
            }
        });

        //ПОСМОТРЕТЬ, ИЗМЕНИТЬ И УДАЛИТЬ ГРУППУ
        groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, final long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity3.this);
                alert.setTitle(((TextView) view).getText());
                String arrayMenu[] = {"Посмотреть", "Редактировать", "Повторить группу", "Удалить группу"};
                alert.setItems(arrayMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //ЗАПРОС В БД ДЛЯ ПОЛУЧЕНИЯ ID НУЖНОЙ ГРУППЫ
                        Cursor cursor4 = database.query(DBHelper.TABLE_GROUPS, new String[] {DBHelper.GR_ID}, "grline_id = ?", new String[] {String.valueOf(idLine)}, null, null, null);
                        cursor4.moveToPosition(position);
                        int groupIndex = cursor4.getColumnIndex(DBHelper.LN_ID);
                        final int groupId = cursor4.getInt(groupIndex);
                        cursor4.close();

                        //ПОСМОТРЕТЬ
                        if (which == 0) {
                            //ПОЛУЧЕНИЕ ИНФОРМАЦИИ О ГРУППЕ
                            String mark = "", vein = "", section = "", workU = "", r = "", u = "", a_b = "", b_c = "", c_a = "", a_n = "", b_n = "", c_n = "", a_pe = "", b_pe = "", c_pe = "", n_pe = "", conclusion = "";
                            Cursor cursor1 = database.query(DBHelper.TABLE_GROUPS, new String[] {DBHelper.GR_ID, DBHelper.GR_MARK,
                                    DBHelper.GR_VEIN, DBHelper.GR_SECTION, DBHelper.GR_U1,
                                    DBHelper.GR_U2, DBHelper.GR_R, DBHelper.GR_A_B, DBHelper.GR_B_C, DBHelper.GR_C_A,
                                    DBHelper.GR_A_N, DBHelper.GR_B_N, DBHelper.GR_C_N, DBHelper.GR_A_PE,
                                    DBHelper.GR_B_PE, DBHelper.GR_C_PE, DBHelper.GR_N_PE, DBHelper.GR_CONCLUSION}, "_id = ?", new String[] {String.valueOf(groupId)}, null, null, null);
                            if (cursor1.moveToFirst()) {
                                int markIndex = cursor1.getColumnIndex(DBHelper.GR_MARK);
                                int veinIndex = cursor1. getColumnIndex(DBHelper.GR_VEIN);
                                int sectionIndex = cursor1. getColumnIndex(DBHelper.GR_SECTION);
                                int workUIndex = cursor1. getColumnIndex(DBHelper.GR_U1);
                                int uIndex = cursor1. getColumnIndex(DBHelper.GR_U2);
                                int rIndex = cursor1. getColumnIndex(DBHelper.GR_R);
                                int a_bIndex = cursor1. getColumnIndex(DBHelper.GR_A_B);
                                int b_cIndex = cursor1. getColumnIndex(DBHelper.GR_B_C);
                                int c_aIndex = cursor1. getColumnIndex(DBHelper.GR_C_A);
                                int a_nIndex = cursor1. getColumnIndex(DBHelper.GR_A_N);
                                int b_nIndex = cursor1. getColumnIndex(DBHelper.GR_B_N);
                                int c_nIndex = cursor1. getColumnIndex(DBHelper.GR_C_N);
                                int a_peIndex = cursor1. getColumnIndex(DBHelper.GR_A_PE);
                                int b_peIndex = cursor1. getColumnIndex(DBHelper.GR_B_PE);
                                int c_peIndex = cursor1. getColumnIndex(DBHelper.GR_C_PE);
                                int n_peIndex = cursor1. getColumnIndex(DBHelper.GR_N_PE);
                                int conclusionIndex = cursor1. getColumnIndex(DBHelper.GR_CONCLUSION);
                                do {
                                    mark = cursor1.getString(markIndex);
                                    vein = cursor1.getString(veinIndex);
                                    section = cursor1.getString(sectionIndex);
                                    workU = cursor1.getString(workUIndex);
                                    u = cursor1.getString(uIndex);
                                    r = cursor1.getString(rIndex);
                                    a_b = cursor1.getString(a_bIndex);
                                    b_c = cursor1.getString(b_cIndex);
                                    c_a = cursor1.getString(c_aIndex);
                                    a_n = cursor1.getString(a_nIndex);
                                    b_n = cursor1.getString(b_nIndex);
                                    c_n = cursor1.getString(c_nIndex);
                                    a_pe = cursor1.getString(a_peIndex);
                                    b_pe = cursor1.getString(b_peIndex);
                                    c_pe = cursor1.getString(c_peIndex);
                                    n_pe = cursor1.getString(n_peIndex);
                                    conclusion = cursor1.getString(conclusionIndex);
                                } while (cursor1.moveToNext());
                            }
                            cursor1.close();
                            AlertDialog.Builder builder4 = new AlertDialog.Builder(InsulationActivity3.this);
                            builder4.setCancelable(false);
                            builder4.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                            builder4.setMessage("Раб. напряжение: " + workU + "\n" + "Марка: " + mark + "\n" +
                                "Кол-во жил: " + vein + "\n" + "Сечение: " + section + "\n" +
                                "Напряж. мегаомметра: " + u + "\n" + "Доп. сопротивление: " + r + "\n" + "A-B: " + a_b + "\n" +
                                "B-C: " + b_c + "\n" + "C-A: " + c_a + "\n" + "A-N: " + a_n + "\n" + "B-N: " + b_n + "\n" +
                                "C-N: " + c_n + "\n" + "A-PE: " + a_pe + "\n" + "B-PE: " + b_pe + "\n" +
                                "C-PE: " + c_pe + "\n" + "N-PE: " + n_pe + "\n" + "Вывод: " + conclusion);
                            builder4.setTitle(((TextView) view).getText());
                            AlertDialog dialog4 = builder4.create();
                            dialog4.show();
                        }

                        //ИЗМЕНИТЬ
                        if (which == 1) {
                            Intent intent = new Intent("android.intent.action.Insulation4");
                            intent.putExtra("nameRoom", nameRoom);
                            intent.putExtra("idRoom", idRoom);
                            intent.putExtra("nameLine", nameLine);
                            intent.putExtra("idLine", idLine);
                            intent.putExtra("nameGroup", "Группа №" + ((TextView) view).getText().toString().substring(3, ((TextView) view).getText().toString().indexOf(" ", 3)));
                            intent.putExtra("idGroup", groupId);
                            startActivity(intent);
                        }

                        //ПОВТОРИТЬ ГРУППУ
                        if (which == 2) {
                            final String nameGroup = "Гр " + String.valueOf(groups.getAdapter().getCount() + 1);
                            String nameMark = "", numberVein = "", numberSection = "", numberWorkU = "", numberU = "", numberR = "";
                            //ЗАПРОС В БД ДЛЯ ПОЛУЧЕНИЯ ИНФО О ГРУППЕ
                            Cursor cursor = database.query(DBHelper.TABLE_GROUPS, new String[] {DBHelper.GR_MARK,
                                    DBHelper.GR_VEIN, DBHelper.GR_SECTION, DBHelper.GR_U1,
                                    DBHelper.GR_U2, DBHelper.GR_R}, "_id= ?", new String[] {String.valueOf(groupId)}, null, null, null);
                            if (cursor.moveToFirst()) {
                                int markIndex = cursor.getColumnIndex(DBHelper.GR_MARK);
                                int veinIndex = cursor.getColumnIndex(DBHelper.GR_VEIN);
                                int sectionIndex = cursor.getColumnIndex(DBHelper.GR_SECTION);
                                int workUIndex = cursor.getColumnIndex(DBHelper.GR_U1);
                                int uIndex = cursor.getColumnIndex(DBHelper.GR_U2);
                                int rIndex = cursor.getColumnIndex(DBHelper.GR_R);
                                nameMark = cursor.getString(markIndex);
                                numberVein = cursor.getString(veinIndex);
                                numberSection = cursor.getString(sectionIndex);
                                numberWorkU = cursor.getString(workUIndex);
                                numberU = cursor.getString(uIndex);
                                numberR = cursor.getString(rIndex);
                            }
                            cursor.close();

                            //ЕСЛИ ОНА РЕЗЕРВ
                            if (nameMark.equals("резерв")) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity3.this);
                                alert.setCancelable(false);
                                alert.setMessage("Вы точно хотите добавить линию(резерв)?");
                                alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(DBHelper.GR_LINE_ID, idLine);
                                        contentValues.put(DBHelper.GR_NAME, nameGroup);
                                        contentValues.put(DBHelper.GR_U1, "-");
                                        contentValues.put(DBHelper.GR_MARK, "резерв");
                                        contentValues.put(DBHelper.GR_VEIN, "-");
                                        contentValues.put(DBHelper.GR_SECTION, "-");
                                        contentValues.put(DBHelper.GR_U2, "-");
                                        contentValues.put(DBHelper.GR_R, "-");
                                        contentValues.put(DBHelper.GR_PHASE, "-");
                                        contentValues.put(DBHelper.GR_A_B, "-");
                                        contentValues.put(DBHelper.GR_B_C, "-");
                                        contentValues.put(DBHelper.GR_C_A, "-");
                                        contentValues.put(DBHelper.GR_A_N, "-");
                                        contentValues.put(DBHelper.GR_B_N, "-");
                                        contentValues.put(DBHelper.GR_C_N, "-");
                                        contentValues.put(DBHelper.GR_A_PE, "-");
                                        contentValues.put(DBHelper.GR_B_PE, "-");
                                        contentValues.put(DBHelper.GR_C_PE, "-");
                                        contentValues.put(DBHelper.GR_N_PE, "-");
                                        contentValues.put(DBHelper.GR_CONCLUSION, "-");
                                        database.insert(DBHelper.TABLE_GROUPS, null, contentValues);
                                        addSpisokGroups(database, groups, idLine);
                                    }
                                });
                                alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                    }
                                });
                                alert.show();
                            }
                            //ЕСЛИ НЕ РЕЗЕРВ
                            else {
                                changePhase(groups, database, idLine, nameGroup,
                                        numberWorkU, nameMark, numberVein, numberSection, numberU, numberR);
                            }
                        }

                        //УДАЛИТЬ ГРУППУ
                        if (which == 3) {

                            //ПОДТВЕРЖДЕНИЕ
                            AlertDialog.Builder builder4 = new AlertDialog.Builder(InsulationActivity3.this);
                            builder4.setCancelable(false);
                            builder4.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    database.delete(DBHelper.TABLE_GROUPS, "_id = ?", new String[] {String.valueOf(groupId)});
                                    //ВЫЧИТАЕМ ИЗ НАЗВАНИЙ ГРУПП ЕДИНИЦУ
                                    Cursor cursor = database.query(DBHelper.TABLE_GROUPS, new String[] {DBHelper.GR_ID, DBHelper.GR_NAME, DBHelper.GR_LINE_ID}, "_id > ?", new String[] {String.valueOf(groupId)}, null, null, null);
                                    if (cursor.moveToFirst()) {
                                        int namechangeIndex = cursor.getColumnIndex(DBHelper.GR_NAME);
                                        int groupidIndex = cursor.getColumnIndex(DBHelper.GR_ID);
                                        int grLineIdIndex = cursor.getColumnIndex(DBHelper.GR_LINE_ID);
                                        do {
                                            if (cursor.getInt(grLineIdIndex) == idLine) {
                                                //В НАЗВАНИИ
                                                ContentValues uppnameGroup = new ContentValues();
                                                uppnameGroup.put(DBHelper.GR_NAME, "Гр " + String.valueOf(Integer.parseInt(cursor.getString(namechangeIndex).substring(3)) - 1));
                                                database.update(DBHelper.TABLE_GROUPS,
                                                        uppnameGroup,
                                                        "_id = ?",
                                                        new String[]{cursor.getString(groupidIndex)});
                                            }
                                        } while (cursor.moveToNext());
                                    }
                                    cursor.close();
                                    //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА ГРУПП
                                    addSpisokGroups(database, groups, idLine);
                                    Toast toast2 = Toast.makeText(getApplicationContext(),
                                            "Группа удалена", Toast.LENGTH_SHORT);
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
                Intent intent = new Intent("android.intent.action.Insulation2");
                intent.putExtra("nameRoom", nameRoom);
                intent.putExtra("idRoom", idRoom);
                startActivity(intent);
                return true;
            case R.id.action_main:
                Intent intent1 = new Intent(InsulationActivity3.this, MainActivity.class);
                startActivity(intent1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getRandomNumber(String x) {
        if (x.contains(","))
            return x;
        int oldNumb = Integer.parseInt(x);
        int random = 0;
        Random generator = new Random();
        if (oldNumb < 300)
            random = oldNumb;
        if (300 <= oldNumb && oldNumb < 500)
            random = (generator.nextInt(9) - 4) * 50 + oldNumb;
        if (500 <= oldNumb && oldNumb < 1000)
            random = (generator.nextInt(5) - 2) * 100 + oldNumb;
        if (1000 <= oldNumb && oldNumb < 3000 )
            random = (generator.nextInt(9) - 4) * 100 + oldNumb;
        if (3000 <= oldNumb)
            random = (generator.nextInt(11) - 5) * 200 + oldNumb;
        return String.valueOf(random);
    }

    //РЕКУРСИВНАЯ ИЗМЕНА ФАЗЫ
    void changePhase(final ListView groups, final SQLiteDatabase database, final int idLine, final String nameGroup, final String numberWorkU, final String nameMark, final String numberVein, final String numberSection, final String numberU, final String numberR) {
        AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity3.this);
        View myView = getLayoutInflater().inflate(R.layout.dialog_for_repeat_group,null);
        alert.setCancelable(false);
        if (numberVein.equals("2") || numberVein.equals("3"))
            alert.setTitle("Выберете фазу и введите значение:");
        else
            alert.setTitle("Введите значение сопротивления:");
        final RadioGroup grradio = myView.findViewById(R.id.radioGroup);
        final RadioButton phaseA = myView.findViewById(R.id.phaseA);
        final RadioButton phaseB = myView.findViewById(R.id.phaseB);
        RadioButton phaseC = myView.findViewById(R.id.phaseC);
        final EditText input = myView.findViewById(R.id.editText2);
        if (numberVein.equals("4") || numberVein.equals("5"))
            grradio.setVisibility(View.GONE);
        alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String numb = input.getText().toString();
                if (numb.equals("")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity3.this);
                    alert.setCancelable(false);
                    alert.setMessage("Заполните поле значения!");
                    alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            changePhase(groups, database, idLine, nameGroup,
                                    numberWorkU, nameMark, numberVein, numberSection, numberU, numberR);
                        }
                    });
                    alert.show();
                }
                else
                    if (numb.contains(",") && Double.parseDouble(numb.replace(",",".")) > 1) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity3.this);
                        alert.setCancelable(false);
                        alert.setMessage("Число не может быть дробным, если оно больше единицы!");
                        alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                changePhase(groups, database, idLine, nameGroup,
                                        numberWorkU, nameMark, numberVein, numberSection, numberU, numberR);
                            }
                        });
                        alert.show();
                    }
                    else {
                        String namePhase;
                        if (numberVein.equals("2") || numberVein.equals("3")) {
                            if (phaseA.isChecked())
                                namePhase = "A";
                            else
                                if (phaseB.isChecked())
                                    namePhase = "B";
                                else
                                    namePhase = "C";
                        }
                        else
                            namePhase = "-";
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBHelper.GR_LINE_ID, idLine);
                        contentValues.put(DBHelper.GR_NAME, nameGroup);
                        contentValues.put(DBHelper.GR_U1, numberWorkU);
                        contentValues.put(DBHelper.GR_MARK, nameMark);
                        contentValues.put(DBHelper.GR_VEIN, numberVein);
                        contentValues.put(DBHelper.GR_SECTION, numberSection);
                        contentValues.put(DBHelper.GR_U2, numberU);
                        contentValues.put(DBHelper.GR_R, numberR);
                        contentValues.put(DBHelper.GR_PHASE, namePhase);
                        if (Double.parseDouble(numb.replace(",", ".")) >= Double.parseDouble(numberR.replace(",", ".")))
                            contentValues.put(DBHelper.GR_CONCLUSION, "соответст.");
                        else
                            contentValues.put(DBHelper.GR_CONCLUSION, "не соотв.");
                        //2 ЖИЛЫ
                        if (numberVein.equals("2")) {
                            contentValues.put(DBHelper.GR_A_B, "-");
                            contentValues.put(DBHelper.GR_B_C, "-");
                            contentValues.put(DBHelper.GR_C_A, "-");
                            contentValues.put(DBHelper.GR_A_PE, "-");
                            contentValues.put(DBHelper.GR_B_PE, "-");
                            contentValues.put(DBHelper.GR_C_PE, "-");
                            contentValues.put(DBHelper.GR_N_PE, "-");
                            if (namePhase.equals("A")) {
                                contentValues.put(DBHelper.GR_A_N, numb);
                                contentValues.put(DBHelper.GR_B_N, "-");
                                contentValues.put(DBHelper.GR_C_N, "-");
                            }
                            if (namePhase.equals("B")) {
                                contentValues.put(DBHelper.GR_A_N, "-");
                                contentValues.put(DBHelper.GR_B_N, numb);
                                contentValues.put(DBHelper.GR_C_N, "-");
                            }
                            if (namePhase.equals("C")) {
                                contentValues.put(DBHelper.GR_A_N, "-");
                                contentValues.put(DBHelper.GR_B_N, "-");
                                contentValues.put(DBHelper.GR_C_N, numb);
                            }
                        }
                        //3 ЖИЛЫ
                        if (numberVein.equals("3")) {
                            contentValues.put(DBHelper.GR_A_B, "-");
                            contentValues.put(DBHelper.GR_B_C, "-");
                            contentValues.put(DBHelper.GR_C_A, "-");
                            if (namePhase.equals("A")) {
                                contentValues.put(DBHelper.GR_A_N, numb);
                                contentValues.put(DBHelper.GR_B_N, "-");
                                contentValues.put(DBHelper.GR_C_N, "-");
                                contentValues.put(DBHelper.GR_A_PE, getRandomNumber(numb));
                                contentValues.put(DBHelper.GR_B_PE, "-");
                                contentValues.put(DBHelper.GR_C_PE, "-");
                                contentValues.put(DBHelper.GR_N_PE, getRandomNumber(numb));
                            }
                            if (namePhase.equals("B")) {
                                contentValues.put(DBHelper.GR_A_N, "-");
                                contentValues.put(DBHelper.GR_B_N, numb);
                                contentValues.put(DBHelper.GR_C_N, "-");
                                contentValues.put(DBHelper.GR_A_PE, "-");
                                contentValues.put(DBHelper.GR_B_PE, getRandomNumber(numb));
                                contentValues.put(DBHelper.GR_C_PE, "-");
                                contentValues.put(DBHelper.GR_N_PE, getRandomNumber(numb));
                            }
                            if (namePhase.equals("C")) {
                                contentValues.put(DBHelper.GR_A_N, "-");
                                contentValues.put(DBHelper.GR_B_N, "-");
                                contentValues.put(DBHelper.GR_C_N, numb);
                                contentValues.put(DBHelper.GR_A_PE, "-");
                                contentValues.put(DBHelper.GR_B_PE, "-");
                                contentValues.put(DBHelper.GR_C_PE, getRandomNumber(numb));
                                contentValues.put(DBHelper.GR_N_PE, getRandomNumber(numb));
                            }
                        }
                        //4 ЖИЛЫ
                        if (numberVein.equals("4")) {
                            contentValues.put(DBHelper.GR_A_B, numb);
                            contentValues.put(DBHelper.GR_B_C, getRandomNumber(numb));
                            contentValues.put(DBHelper.GR_C_A, getRandomNumber(numb));
                            contentValues.put(DBHelper.GR_A_N, getRandomNumber(numb));
                            contentValues.put(DBHelper.GR_B_N, getRandomNumber(numb));
                            contentValues.put(DBHelper.GR_C_N, getRandomNumber(numb));
                            contentValues.put(DBHelper.GR_A_PE, "-");
                            contentValues.put(DBHelper.GR_B_PE, "-");
                            contentValues.put(DBHelper.GR_C_PE, "-");
                            contentValues.put(DBHelper.GR_N_PE, "-");
                        }
                        //5 ЖИЛ
                        if (numberVein.equals("5")) {
                            contentValues.put(DBHelper.GR_A_B, numb);
                            contentValues.put(DBHelper.GR_B_C, getRandomNumber(numb));
                            contentValues.put(DBHelper.GR_C_A, getRandomNumber(numb));
                            contentValues.put(DBHelper.GR_A_N, getRandomNumber(numb));
                            contentValues.put(DBHelper.GR_B_N, getRandomNumber(numb));
                            contentValues.put(DBHelper.GR_C_N, getRandomNumber(numb));
                            contentValues.put(DBHelper.GR_A_PE, getRandomNumber(numb));
                            contentValues.put(DBHelper.GR_B_PE, getRandomNumber(numb));
                            contentValues.put(DBHelper.GR_C_PE, getRandomNumber(numb));
                            contentValues.put(DBHelper.GR_N_PE, getRandomNumber(numb));
                        }
                        database.insert(DBHelper.TABLE_GROUPS, null, contentValues);
                        addSpisokGroups(database, groups, idLine);
                        Toast toast2 = Toast.makeText(getApplicationContext(),
                                "Группа добавлена", Toast.LENGTH_SHORT);
                        toast2.show();
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

    public void addSpisokGroups(SQLiteDatabase database, ListView groups, int idLine) {
        final ArrayList<String> spisokGroups = new ArrayList <String>();
        Cursor cursor = database.query(DBHelper.TABLE_GROUPS, new String[] {DBHelper.GR_NAME, DBHelper.GR_MARK,
                DBHelper.GR_VEIN, DBHelper.GR_SECTION, DBHelper.GR_PHASE}, "grline_id = ?", new String[] {String.valueOf(idLine)}, null, null, null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.GR_NAME);
            int markIndex = cursor.getColumnIndex(DBHelper.GR_MARK);
            int veinIndex = cursor. getColumnIndex(DBHelper.GR_VEIN);
            int sectionIndex = cursor. getColumnIndex(DBHelper.GR_SECTION);
            int phaseIndex = cursor. getColumnIndex(DBHelper.GR_PHASE);
            do {
                String nameMark = cursor.getString(markIndex);
                String numberVein = cursor.getString(veinIndex);
                if (nameMark.equals("резерв"))
                    spisokGroups.add(cursor.getString(nameIndex) + " (Резерв)");
                else
                    if (Integer.parseInt(numberVein) == 2 || Integer.parseInt(numberVein) == 3)
                        spisokGroups.add(cursor.getString(nameIndex) + " (" + nameMark + "; " + numberVein + "x" + cursor.getString(sectionIndex) +
                            "; Фаза " + cursor.getString(phaseIndex) + ")");
                    else
                        spisokGroups.add(cursor.getString(nameIndex) + " (" + nameMark + "; " + numberVein + "x" + cursor.getString(sectionIndex) + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spisokGroups);
        groups.setAdapter(adapter);
    }
}
