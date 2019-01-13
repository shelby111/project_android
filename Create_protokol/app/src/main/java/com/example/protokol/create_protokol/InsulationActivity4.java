package com.example.protokol.create_protokol;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class InsulationActivity4 extends AppCompatActivity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insulation4);

        dbHelper = new DBHelper(this);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        final TextView group = findViewById(R.id.textView8);
        final Switch reserve = findViewById(R.id.switch3);
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
        final TextView r = findViewById(R.id.textView17);
        Button chooseR = findViewById(R.id.button15);
        final TextView phase = findViewById(R.id.textView18);
        Button choosePhase = findViewById(R.id.button16);
        final EditText number = findViewById(R.id.editText2);
        Button save = findViewById(R.id.button17);

        final String nameRoom = getIntent().getStringExtra("nameRoom");
        final long idRoom = getIntent().getLongExtra("idRoom", 0);
        final String nameLine = getIntent().getStringExtra("nameLine");
        final int idLine = getIntent().getIntExtra("idLine", 0);
        String nameGroup = getIntent().getStringExtra("nameGroup");
        final int idGroup = getIntent().getIntExtra("idGroup", 0);
        final boolean change = getIntent().getBooleanExtra("change", false);

        //ДЕЛАЕМ ПЕРЕКЛЮЧАТЕЛЬ ВЫКЛЮЧЕННЫМ ПО УМОЛЧАНИЮ
        reserve.setChecked(false);
        
        //ЗАПОЛНЕНИЕ ДАННЫХ, ЕСЛИ ОНИ ПЕРЕДАНЫ
        group.setText(nameGroup);
        if (change) {
            //ЗАПРОС В БД(ПОЛУЧЕНИЕ ИНФОРМАЦИИ О ГРУППЕ)
            Cursor cursor = database.query(DBHelper.TABLE_GROUPS, new String[] {DBHelper.GR_ID, DBHelper.GR_MARK,
                    DBHelper.GR_VEIN, DBHelper.GR_SECTION, DBHelper.GR_U1,
                    DBHelper.GR_U2, DBHelper.GR_R, DBHelper.GR_PHASE, DBHelper.GR_A_B,
                    DBHelper.GR_A_N, DBHelper.GR_B_N, DBHelper.GR_C_N}, "_id = ?", new String[] {String.valueOf(idGroup)}, null, null, null);
            if (cursor.moveToFirst()) {
                int markIndex = cursor.getColumnIndex(DBHelper.GR_MARK);
                int veinIndex = cursor. getColumnIndex(DBHelper.GR_VEIN);
                int sectionIndex = cursor. getColumnIndex(DBHelper.GR_SECTION);
                int workUIndex = cursor. getColumnIndex(DBHelper.GR_U1);
                int uIndex = cursor. getColumnIndex(DBHelper.GR_U2);
                int rIndex = cursor. getColumnIndex(DBHelper.GR_R);
                int phaseIndex = cursor. getColumnIndex(DBHelper.GR_PHASE);
                int a_bIndex = cursor. getColumnIndex(DBHelper.GR_A_B);
                int a_nIndex = cursor. getColumnIndex(DBHelper.GR_A_N);
                int b_nIndex = cursor. getColumnIndex(DBHelper.GR_B_N);
                int c_nIndex = cursor. getColumnIndex(DBHelper.GR_C_N);
                do {
                    //ЗАПОЛНЕНИЕ ДАННЫХ
                    mark.setText("Марка: " + cursor.getString(markIndex));
                    //ЕСЛИ РЕЗЕРВ
                    if (cursor.getString(markIndex).equals("резерв")) {
                        reserve.setChecked(true);
                        vein.setText("Кол-во жил: -");
                        section.setText("Сеченеие: -");
                        workU.setText("Рабочее напряжение: -");
                        u.setText("Напряжение мегаомметра: -");
                        r.setText("Сопротивление: -");
                        phase.setText("Фаза: -");
                        number.setText("-");
                        break;
                    }
                    else {
                        String namePhase = cursor.getString(phaseIndex);
                        vein.setText("Кол-во жил: " + cursor.getString(veinIndex));
                        section.setText("Сечение: " + cursor.getString(sectionIndex));
                        workU.setText("Рабочее напряжение: " + cursor.getString(workUIndex));
                        u.setText("Напряжение мегаомметра: " + cursor.getString(uIndex));
                        r.setText("Сопротивление: " + cursor.getString(rIndex));
                        phase.setText("Фаза: " + namePhase);
                        //ЗАПОЛНЕНИЕ ЗНАЧЕНИЯ
                        switch (namePhase) {
                            case "A":
                                number.setText(cursor.getString(a_nIndex));
                                break;
                            case "B":
                                number.setText(cursor.getString(b_nIndex));
                                break;
                            case "C":
                                number.setText(cursor.getString(c_nIndex));
                                break;
                            default:
                                number.setText(cursor.getString(a_bIndex));
                                break;
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        //ИЗМЕНЕНИЕ ПЕРЕКЛЮЧАТЕЛЯ
        reserve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mark.setText("Марка: резерв");
                    vein.setText("Кол-во жил: -");
                    section.setText("Сечение: -");
                    workU.setText("Рабочее напряжение: -");
                    u.setText("Напряжение мегаомметра: -");
                    r.setText("Сопротивление: -");
                    phase.setText("Фаза: -");
                    number.setText("-");
                }
                else {
                    mark.setText("Марка: Не выбрана");
                    vein.setText("Кол-во жил: Не выбрано");
                    section.setText("Сечение: Не выбрано");
                    workU.setText("Рабочее напряжение: Не выбрано");
                    u.setText("Напряжение мегаомметра: 1000");
                    r.setText("Сопротивление: 0,5");
                    phase.setText("Фаза: Не выбрана");
                    number.setText("");
                }
            }
        });

        //ВЫБОР МАРОК
        chooseMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reserve.isChecked()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                    alert.setTitle("Выберете марку:");
                    final String marks[] = {"ПВС", "ВВГ", "АВВГ", "ПУНП", "АПУНП", "ШВВП", "АПВ", "ПВ", "ПВ3"};
                    alert.setItems(marks, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mark.setText("Марка: " + marks[which]);
                        }
                    });
                    alert.setPositiveButton("Ввести", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            AlertDialog.Builder alert1 = new AlertDialog.Builder(InsulationActivity4.this);
                            alert1.setCancelable(false);
                            alert1.setTitle("Введите марку:");
                            final EditText input = new EditText(InsulationActivity4.this);
                            alert1.setView(input);
                            alert1.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String nameMark = input.getText().toString();
                                    mark.setText("Марка: " + nameMark);
                                }
                            });
                            alert1.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                            alert1.show();
                        }
                    });
                    alert.show();
                }
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                    alert.setCancelable(false);
                    alert.setMessage("Выбор марок не доступен, так как группа резервная. " +
                            "Чтобы выбрать марку, нажмите на ползунок, сделав его неактивным");
                    alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                }
            }
        });

        //ВЫБОР КОЛ-ВА ЖИЛ
        chooseVein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reserve.isChecked()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                    alert.setTitle("Выберете кол-во жил:");
                    final String veins[] = {"2", "3", "4", "5"};
                    alert.setItems(veins, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            vein.setText("Кол-во жил: " + veins[which]);
                            if (which == 2 || which == 3)
                                phase.setText("Фаза: -");
                        }
                    });
                    alert.show();
                }
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                    alert.setCancelable(false);
                    alert.setMessage("Выбор кол-ва жил не доступен, так как группа резервная. " +
                            "Чтобы выбрать кол-во жил, нажмите на ползунок, сделав его неактивным");
                    alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                }
            }
        });

        //ВЫБОР СЕЧЕНИЯ
        chooseSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reserve.isChecked()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                    alert.setTitle("Выберете сечение:");
                    final String sectoins[] = {"0,5", "0,75", "1", "1,5", "2,5", "4", "6", "10", "16", "25", "35", "50", "70", "95", "120", "150", "185", "240"};
                    alert.setItems(sectoins, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            section.setText("Сечение: " + sectoins[which]);
                        }
                    });
                    alert.setPositiveButton("Ввести", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            AlertDialog.Builder alert1 = new AlertDialog.Builder(InsulationActivity4.this);
                            View myView = getLayoutInflater().inflate(R.layout.dialog_for_section,null);
                            alert1.setCancelable(false);
                            alert1.setTitle("Введите сечение:");
                            final EditText input = myView.findViewById(R.id.editText2);
                            alert1.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String numberSection = input.getText().toString();
                                    section.setText("Сечение: " + numberSection);
                                }
                            });
                            alert1.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                            alert1.setView(myView);
                            alert1.show();
                        }
                    });
                    alert.show();
                }
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                    alert.setCancelable(false);
                    alert.setMessage("Выбор сечения не доступен, так как группа резервная. " +
                            "Чтобы выбрать сечение, нажмите на ползунок, сделав его неактивным");
                    alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                }
            }
        });

        //ВЫБОР РАБОЧЕГО НАПРЯЖЕНИЯ
        chooseWorkU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reserve.isChecked()) {
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
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                    alert.setCancelable(false);
                    alert.setMessage("Выбор напряжения не доступен, так как группа резервная. " +
                            "Чтобы выбрать напряжение, нажмите на ползунок, сделав его неактивным");
                    alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                }
            }
        });

        //ВЫБОР НАПРЯЖЕНИЯ МЕГАОММЕТРА
        chooseU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reserve.isChecked()) {
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
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                    alert.setCancelable(false);
                    alert.setMessage("Выбор напряжения не доступен, так как группа резервная. " +
                            "Чтобы выбрать напряжение, нажмите на ползунок, сделав его неактивным");
                    alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                }
            }
        });

        //ВЫБОР СОПРОТИВЛЕНИЯ
        chooseR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reserve.isChecked()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                    alert.setTitle("Выберете сопротивление:");
                    final String arrR[] = {"0,5", "1"};
                    alert.setItems(arrR, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            r.setText("Сопротивление: " + arrR[which]);
                        }
                    });
                    alert.show();
                }
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                    alert.setCancelable(false);
                    alert.setMessage("Выбор сопротивления не доступен, так как группа резервная. " +
                            "Чтобы выбрать сопротивление, нажмите на ползунок, сделав его неактивным");
                    alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                }
            }
        });

        //ВЫБОР ФАЗЫ
        choosePhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((vein.getText().toString().substring(12).equals("4") || vein.getText().toString().substring(12).equals("5"))) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                    alert.setCancelable(false);
                    alert.setMessage("Выбор фазы доступен, если количество жил равно 2 или 3. " +
                            "При ином количестве жил переходите сразу к вводу значения.");
                    alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                }
                else {
                    if (!reserve.isChecked()) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                        alert.setTitle("Выберете фазу:");
                        final String phases[] = {"A", "B", "C"};
                        alert.setItems(phases, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                phase.setText("Фаза: " + phases[which]);
                            }
                        });
                        alert.show();
                    }
                    else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                        alert.setCancelable(false);
                        alert.setMessage("Выбор фазы не доступен, так как группа резервная. " +
                                "Чтобы выбрать фазу, нажмите на ползунок, сделав его неактивным");
                        alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                        alert.show();
                    }
                }
            }
        });

        //ДОБАВЛЕНИЕ ГРУППЫ (СОХРАНЕНИЕ ДАННЫХ)
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textName = group.getText().toString();
                String textMark = mark.getText().toString();
                String textVein = vein.getText().toString();
                String textSection = section.getText().toString();
                String textWorkU = workU.getText().toString();
                String textU = u.getText().toString();
                String textR = r.getText().toString();
                String textPhase = phase.getText().toString();
                String numb = number.getText().toString();
                //ПРОВЕРКА НА ВВОД ВСЕХ ДАННЫХ
                if (numb.equals("") || textMark.equals("Марка: Не выбрана") || textVein.equals("Кол-во жил: Не выбрано") ||
                    textSection.equals("Сечение: Не выбрано") || textWorkU.equals("Рабочее напряжение: Не выбрано") ||
                    textPhase.equals("Фаза: Не выбрана") || (textVein.equals("Кол-во жил: 2") && textPhase.equals("Фаза: -")) ||
                    (textVein.equals("Кол-во жил: 3") && textPhase.equals("Фаза: -"))) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                    alert.setCancelable(false);
                    alert.setMessage("Заполните все поля!");
                    alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
                    alert.show();
                }
                else {
                    if (numb.contains(",") && Double.parseDouble(numb.replace(",",".")) > 1) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(InsulationActivity4.this);
                        alert.setCancelable(false);
                        alert.setMessage("Число не может быть дробным, если оно больше единицы!");
                        alert.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                        alert.show();
                    }
                    else {
                        //УДАЛИМ НАШУ ГРУППУ, ЕСЛИ МЫ ВНОСИМ ИЗМЕНЕНИЯ, А НЕ СОЗДАЕМ НОВУЮ
                        if (change)
                            database.delete(DBHelper.TABLE_GROUPS, "_id = ?", new String[]{String.valueOf(idGroup)});
                        //СОЗДАЕМ НОВУЮ ГРУППУ
                        if (reserve.isChecked()) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DBHelper.GR_LINE_ID, idLine);
                            contentValues.put(DBHelper.GR_LNR_ID, idRoom);
                            contentValues.put(DBHelper.GR_ID, idGroup);
                            contentValues.put(DBHelper.GR_NAME, "Гр " + Integer.parseInt(textName.substring(8)));
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
                        } else {
                            String namePhase = textPhase.substring(6);
                            String numberVein = textVein.substring(12);
                            String numberR = textR.substring(15);
                            //ЗАПОЛНЕНИЕ НОВОЙ СТРОКИ
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DBHelper.GR_LINE_ID, idLine);
                            contentValues.put(DBHelper.GR_LNR_ID, idRoom);
                            contentValues.put(DBHelper.GR_ID, idGroup);
                            contentValues.put(DBHelper.GR_NAME, "Гр " + Integer.parseInt(textName.substring(8)));
                            contentValues.put(DBHelper.GR_U1, textWorkU.substring(20));
                            contentValues.put(DBHelper.GR_MARK, textMark.substring(7));
                            contentValues.put(DBHelper.GR_VEIN, numberVein);
                            contentValues.put(DBHelper.GR_SECTION, textSection.substring(9));
                            contentValues.put(DBHelper.GR_U2, textU.substring(24));
                            contentValues.put(DBHelper.GR_R, numberR);
                            contentValues.put(DBHelper.GR_PHASE, namePhase);
                            if (Double.parseDouble(numb.replace(",", ".")) >= Double.parseDouble(numberR.replace(",", ".")))
                                contentValues.put(DBHelper.GR_CONCLUSION, "соответств.");
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
                        }
                        Toast toast2 = Toast.makeText(getApplicationContext(),
                                "Данные сохранены", Toast.LENGTH_SHORT);
                        toast2.show();
                        //ПЕРЕХОД К СПИСКАМ ГРУПП
                        Intent intent = new Intent("android.intent.action.Insulation3");
                        intent.putExtra("nameRoom", nameRoom);
                        intent.putExtra("idRoom", idRoom);
                        intent.putExtra("nameLine", nameLine);
                        intent.putExtra("idLine", idLine);
                        startActivity(intent);
                    }
                }
            }
        });
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
}