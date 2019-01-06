package com.example.protokol.create_protokol;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class InsulationActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insulation4);

        TextView nameGroup = findViewById(R.id.textView8);
        Switch reserve = findViewById(R.id.switch3);
        final TextView mark = findViewById(R.id.textView12);
        Button chooseMark = findViewById(R.id.button10);
        final TextView vein = findViewById(R.id.textView13);
        Button chooseVein = findViewById(R.id.button11);
        final TextView section = findViewById(R.id.textView14);
        Button chooseSection = findViewById(R.id.button12);
        final TextView workU = findViewById(R.id.textView15);
        Button chooseWorkU = findViewById(R.id.button13);
        final TextView u = findViewById(R.id.textView16);
        Button chooseU = findViewById(R.id.button14);
        final TextView phase = findViewById(R.id.textView17);
        Button choosePhase = findViewById(R.id.button15);
        EditText number = findViewById(R.id.editText2);
        Button save = findViewById(R.id.button16);

        //ДЕЛАЕМ ПЕРЕКЛЮЧАТЕЛЬ ВЫКЛЮЧЕННЫМ

        //ЗАПОЛНЕНИЕ ДАННЫХ, ЕСЛИ ОНИ ПЕРЕДАНЫ

        //ВЫБОР МАРОК
        chooseMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                alert.setTitle("Выберете марку:");
                final String marks[] = {"ПВС", "ВВГ", "АВВГ", "ПУНП", "АПУНП", "ШВВП", "АПВ", "ПВ", "ПВ3"};
                alert.setItems(marks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mark.setText("Марка: " + marks[which]);
                    }
                });
                alert.show();
            }
        });

        //ВЫБОР КОЛ-ВА ЖИЛ
        chooseVein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                alert.setTitle("Выберете кол-во жил:");
                final String veins[] = {"2", "3", "4", "5"};
                alert.setItems(veins, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vein.setText("Кол-во жил: " + veins[which]);
                    }
                });
                alert.show();
            }
        });

        //ВЫБОР СЕЧЕНИЯ
        chooseSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                alert.setTitle("Выберете сечение:");
                final String sectoins[] = {"0,5", "0,75", "1", "1,5", "2,5", "4", "6", "10", "16", "25", "35", "50", "70", "95", "120", "150", "185", "240"};
                alert.setItems(sectoins, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        section.setText("Сечение: " + sectoins[which]);
                    }
                });
                alert.show();
            }
        });

        //ВЫБОР РАБОЧЕГО НАПРЯЖЕНИЯ
        chooseWorkU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                alert.setTitle("Выберете рабочее напряжение:");
                final String arrWorkU[] = {"220", "380"};
                alert.setItems(arrWorkU, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        workU.setText("Рабочее напряжение: " + arrWorkU[which]);
                    }
                });
                alert.show();
            }
        });

        //ВЫБОР НАПРЯЖЕНИЯ МЕГАОММЕТРА
        chooseU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                alert.setTitle("Выберете напряжение мегаомметра:");
                final String arrU[] = {"500", "1000", "2500"};
                alert.setItems(arrU, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        u.setText("Напряжение мегаомметра: " + arrU[which]);
                    }
                });
                alert.show();
            }
        });

        //ВЫБОР ФАЗЫ
        choosePhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                alert.setTitle("Выберете фазу:");
                final String phases[] = {"Фаза A", "Фаза B", "Фаза C"};
                alert.setItems(phases, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        phase.setText("Напряжение мегаомметра: " + phases[which]);
                    }
                });
                alert.show();
            }
        });

        //ДОБАВЛЕНИЕ ГРУППЫ (СОХРАНЕНИЕ ДАННЫХ)
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
