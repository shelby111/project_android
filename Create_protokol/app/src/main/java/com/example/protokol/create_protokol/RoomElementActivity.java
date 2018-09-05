package com.example.protokol.create_protokol;

import android.app.AlertDialog;
import android.app.Fragment;
import android.R.string;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;

import java.util.ArrayList;


public class RoomElementActivity extends AppCompatActivity {
    private String[]header = {"№ п/п", "Месторасположение и наименование электрооборудования", "Кол-во проверенных элементов", "R перех. допустимое, (Ом)", "R перех. измеренное, (Ом)", "Вывод о соответствии нормативному документу"};
    private String date = "Дата проведения проверки";
    private String zag = "Климатические условия при проведении измерений";
    private String uslovia = "Температура воздуха. Вложность воздуха. Атмосферное давление.";
    private String zag2 = "Нормативные и технические документы, на соответствие требованиям которых проведена проверка:";
    private String ok = "Подтвердить";
    private String cancel = "Отмена";
    private TemplatePDF templatePDF;
    private static final String[] rooms = new String[]{"Электрощитовая","Комната1"};
    private static final String[] elements = new String[]{"Розетка с з.к.","Розетка без з.к."};
    int count = 1;
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
        Button button = (Button)findViewById(R.id.button);
        Button button2 = (Button)findViewById(R.id.button2);
        ImageView image = (ImageView)findViewById(R.id.image);
        ImageView image1 = (ImageView)findViewById(R.id.image1);
        final EditText kolvo = (EditText)findViewById(R.id.kolvo);
        final EditText soprot = (EditText)findViewById(R.id.soprot);

        ArrayAdapter<String>adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, rooms);
        actv1.setAdapter(adapter);
        ArrayAdapter<String>adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, elements);
        actv2.setAdapter(adapter1);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actv1.showDropDown();
            }
        });
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actv2.showDropDown();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nameRoom = actv1.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        templatePDF.addRoom(nameRoom);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Комната <" + nameRoom + "> добавлена", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.setMessage("Название комнаты: " + nameRoom)
                        .setTitle("Подтвердите данные");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                String countstr = String.valueOf(count);
                final String nameElement = actv2.getText().toString();
                String kolvo1 = kolvo.getText().toString();
                String r = soprot.getText().toString();
                final String[]elements = {countstr, nameElement, kolvo1, "5", r, "6"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(RoomElementActivity.this);
                builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        templatePDF.addElement(elements);
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

        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument();
        templatePDF.addMetaData("Protokol", "Item", "Company");
        templatePDF.addTitles("8. РЕЗУЛЬТАТЫ", "проверки наличия цепи между заземленными установками и элементами заземленной установки");
        templatePDF.addParagraph(date);
        templatePDF.addZag(zag);
        templatePDF.addParagraph(uslovia);
        templatePDF.addZag(zag2);
        templatePDF.createTable(header, getClients());
    }

    public void pdfApp(View view) {
        templatePDF.closeDocument();
        templatePDF.appViewPDF(this);
    }
}
