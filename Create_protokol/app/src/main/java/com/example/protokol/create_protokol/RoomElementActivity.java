package com.example.protokol.create_protokol;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    private String[]header = {"№ п/п", "Месторасположение и наименование электрооборудования", "Кол-во проверенных элементов", "R перех. допустимое, (Ом)", "R перех. измеренное, (Ом)", "Вывод о соответствии нормативному документу"};
    private String date = "Дата проведения проверки «__» ___________ _______г. ";
    private String zag = "Климатические условия при проведении измерений";
    private String uslovia = "Температура воздуха __С. Вложность воздуха __%. Атмосферное давление ___ мм.рт.ст.(бар).";
    private String zag2 = "Нормативные и технические документы, на соответствие требованиям которых проведена проверка:";
    private String line = "______________________________________________________________________________________";
    private String zakl = "Заключение:";
    private String end = "a) Проверена целостность и прочность проводников заземления и зануления, переходные контакты их    соединений, болтовые соединения проверены на затяжку, сварные – ударом молотка." + "\n" +
            "b) Сопротивление переходных контактов выше нормы, указаны в п/п ______________." + "\n" +
            "c) Не заземлено оборудование, указанное в п/п _______________________________." + "\n" +
            "d) Величина измеренного переходного сопротивления прочих контактов заземляющих и нулевых проводников,  элементов электрооборудования соответствует (не соответствует) нормам __________________________.";
    private String proverka = "Проверку провели:   _____________________     ___________    _____________" + "\n" +
                              "                                           (Должность)                    (Подпись)          (Ф.И.О.)" + "\n" + "\n" +
                              "Проверил:                 _____________________     ___________    _____________" + "\n" +
                              "                                           (Должность)                    (Подпись)          (Ф.И.О.)";
    private TemplatePDF templatePDF;
    private static final String[] elements = new String[]{"Розетка с з.к.","Розетка без з.к."};
    int count = 0;
    int countroom = 0;
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

        final AutoCompleteTextView actv1 = (AutoCompleteTextView)findViewById(R.id.actv1);
        final AutoCompleteTextView actv2 = (AutoCompleteTextView)findViewById(R.id.actv2);
        Button room = (Button)findViewById(R.id.button);
        Button elem = (Button)findViewById(R.id.button2);
        Button endbtn = (Button)findViewById(R.id.button3);
        Button prosmotr = (Button)findViewById(R.id.button4);
        ImageView image1 = (ImageView)findViewById(R.id.image1);
        final EditText kolvo = (EditText)findViewById(R.id.kolvo);
        final Switch switch1 = (Switch)findViewById(R.id.switch1);

        ArrayAdapter<String>adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, elements);
        actv2.setAdapter(adapter1);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actv2.showDropDown();
            }
        });

        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countroom++;
                final String nameRoom = actv1.getText().toString();
                final String countroomstr = String.valueOf(countroom) + ". ";
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        count = 0;
                        templatePDF.addRoom(nameRoom, countroomstr);
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

        elem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                Boolean switchbool = switch1.isChecked();
                String countstr = String.valueOf(count) + ".";
                String kolvo1 = kolvo.getText().toString();
                String r;
                String res;
                if (switchbool == false) {
                    r = random();
                    res = "соответсвует";
                }else {
                    r = "Н.З.";
                    res = "не соответствует";
                }
                final String nameElement = actv2.getText().toString();
                final String[]elements = {countstr, nameElement, kolvo1, "0,05", r, res};
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        templatePDF.addElement(elements);
                        switch1.setChecked(false);
                        actv2.setText("");
                        kolvo.setText("");
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

        prosmotr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    final String[] catNamesArray = {"Васька", "Рыжик", "Мурзик"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                    builder.setTitle("Выберите комнату")
                            .setItems(catNamesArray, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(RoomElementActivity.this,
                                            "Выбранный кот: " + catNamesArray[which],
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
            }
        });

        endbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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

    public String random() {
        Random generator = new Random();
        int randomIndex = generator.nextInt(soprot.length);
        return soprot[randomIndex];
    }
}
