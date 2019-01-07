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

public class InsulationActivity3 extends AppCompatActivity {

    DBHelper dbHelper;
    private int countgroups = 0;

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
        setContentView(R.layout.activity_insulation3);

        dbHelper = new DBHelper(this);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        ImageView back = findViewById(R.id.imageView);
        TextView room = findViewById(R.id.textView6);
        TextView line = findViewById(R.id.textView7);
        final ListView groups = findViewById(R.id.groups);
        Button addGroup = findViewById(R.id.button9);
        Button pdf = findViewById(R.id.button8);
        final String nameRoom = getIntent().getStringExtra("nameRoom");
        final long idRoom = getIntent().getLongExtra("idRoom", 0);
        final String nameLine = getIntent().getStringExtra("nameLine");
        final int idLine = getIntent().getIntExtra("idLine", 0);

        //ВЫВОД КОМНАТЫ И ЩИТА
        room.setText(nameRoom);
        line.setText(nameLine);

        //ПОЛУЧЕНИЕ ЗНАЧЕНИЯ СOUNTGROUPS
        countgroups = getCountgroup(database);

        //ЗАПРОС В БД И ЗАПОЛНЕНИЕ СПИСКА ГРУПП
        addSpisokGroups(database, groups, idLine);

        //НАЗАД
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.Insulation2");
                intent.putExtra("nameRoom", nameRoom);
                intent.putExtra("idRoom", idRoom);
                startActivity(intent);
            }
        });

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
                intent.putExtra("idGroup", countgroups+1);
                intent.putExtra("change", false);
                startActivity(intent);
            }
        });

        //ИЗМЕНИТЬ И УДАЛИТЬ ГРУППУ
        groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, final long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity3.this);
                alert.setTitle(((TextView) view).getText());
                String arrayMenu[] = {"Посмотреть", "Редактировать", "Удалить группу"};
                alert.setItems(arrayMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //ЗАПРОС В БД ДЛЯ ПОЛУЧЕНИЯ ID НУЖНОЙ ЛИНИИ
                        Cursor cursor4 = database.query(DBHelper.TABLE_GROUPS, new String[] {DBHelper.GR_ID}, "grline_id = ?", new String[] {String.valueOf(idLine)}, null, null, null);
                        cursor4.moveToPosition(position);
                        int groupIndex = cursor4.getColumnIndex(DBHelper.LN_ID);
                        final int groupId = cursor4.getInt(groupIndex);
                        cursor4.close();

                        //ПОСМОТРЕТЬ
                        if (which == 0) {
                            //ПОЛУЧЕНИЕ ИНФОРМАЦИИ О ГРУППЕ
                            String mark = "", vein = "", section = "", workU = "", r = "", u = "", a_b = "", b_c = "", c_a = "", a_n = "", b_n = "", c_n = "", a_pe = "", b_pe = "", c_pe = "", n_pe = "";
                            Cursor cursor1 = database.query(DBHelper.TABLE_GROUPS, new String[] {DBHelper.GR_ID, DBHelper.GR_MARK,
                                    DBHelper.GR_VEIN, DBHelper.GR_SECTION, DBHelper.GR_U1,
                                    DBHelper.GR_U2, DBHelper.GR_A_B, DBHelper.GR_B_C, DBHelper.GR_C_A,
                                    DBHelper.GR_A_N, DBHelper.GR_B_N, DBHelper.GR_C_N, DBHelper.GR_A_PE,
                                    DBHelper.GR_B_PE, DBHelper.GR_C_PE, DBHelper.GR_N_PE}, "_id = ?", new String[] {String.valueOf(groupId)}, null, null, null);
                            if (cursor1.moveToFirst()) {
                                int markIndex = cursor1.getColumnIndex(DBHelper.GR_MARK);
                                int veinIndex = cursor1. getColumnIndex(DBHelper.GR_VEIN);
                                int sectionIndex = cursor1. getColumnIndex(DBHelper.GR_SECTION);
                                int workUIndex = cursor1. getColumnIndex(DBHelper.GR_U1);
                                int uIndex = cursor1. getColumnIndex(DBHelper.GR_U2);
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
                                do {
                                    mark = cursor1.getString(markIndex);
                                    vein = cursor1.getString(veinIndex);
                                    section = cursor1.getString(sectionIndex);
                                    workU = cursor1.getString(workUIndex);
                                    if (mark.equals("резерв"))
                                        r = "-";
                                    else
                                        r = "0,5";
                                    u = cursor1.getString(uIndex);
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
                                "Напряж. мегаомметра: " + u + "\n" + "Сопротивление: " + r + "\n" + "A-B: " + a_b + "\n" +
                                "B-C: " + b_c + "\n" + "C-A: " + c_a + "\n" + "A-N: " + a_n + "\n" + "B-N: " + b_n + "\n" +
                                "C-N: " + c_n + "\n" + "A-PE: " + a_pe + "\n" + "B-PE: " + b_pe + "\n" +
                                "C-PE: " + c_pe + "\n" + "N-PE: " + n_pe);
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
                            intent.putExtra("nameGroup", "Группа №" + ((TextView) view).getText().toString().substring(3));
                            intent.putExtra("idGroup", groupId);
                            intent.putExtra("change", true);
                            startActivity(intent);
                        }

                        //УДАЛИТЬ ГРУППУ
                        if (which == 2) {

                            //ПОДТВЕРЖДЕНИЕ
                            AlertDialog.Builder builder4 = new AlertDialog.Builder(InsulationActivity3.this);
                            builder4.setCancelable(false);
                            builder4.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    database.delete(DBHelper.TABLE_GROUPS, "_id = ?", new String[] {String.valueOf(groupId)});
                                    //ВЫЧИТАЕМ ИЗ ID И НАЗВАНИЙ ГРУПП ЕДИНИЦУ
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
                                            //ID
                                            ContentValues uppidGroup = new ContentValues();
                                            uppidGroup.put(DBHelper.GR_ID, String.valueOf(cursor.getInt(groupidIndex)-1));
                                            database.update(DBHelper.TABLE_GROUPS,
                                                    uppidGroup,
                                                    "_id = ?",
                                                    new String[] {cursor.getString(groupidIndex)});
                                        } while (cursor.moveToNext());
                                    }
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

        //ОТКРЫТИЕ PDF
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start("HORIZONT");
                templatePDF.closeDocument();
                templatePDF.appViewPDF(InsulationActivity3.this);
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

    int getCountgroup(SQLiteDatabase database) {
        int numb_groups = 0;
        Cursor cur = database.rawQuery("select count(*) as numb_groups from groups", new String[] { });
        if (cur.moveToFirst()) {
            int numbers_groupidIndex = cur.getColumnIndex("numb_groups");
            do {
                numb_groups = cur.getInt(numbers_groupidIndex);
            } while (cur.moveToNext());
        }
        cur.close();
        return numb_groups;
    }

    public void addSpisokGroups(SQLiteDatabase database, ListView rooms, long idLine) {
        final ArrayList<String> spisokGroups = new ArrayList <String>();
        Cursor cursor = database.query(DBHelper.TABLE_GROUPS, new String[] {DBHelper.GR_NAME}, "grline_id = ?", new String[] {String.valueOf(idLine)}, null, null, null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.GR_NAME);
            do {
                spisokGroups.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spisokGroups);
        rooms.setAdapter(adapter);
    }
}
