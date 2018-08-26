package com.example.protokol.create_protokol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String[]header = {"Id", "Nombre", "Apellido"};
    private String shortText = "Hola";
    private String longText = "LOOOOOOOOOONG TEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEXT BIIIIIIIIIIIIIIIIIIIIIIIIIIG VERYYYYYYYYYYYYYYYYYYYYYYYY BIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIG AND VEEEEEEEEEEEEEEEEEEEEEEEEEEEEERY LOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOONG";
    private TemplatePDF templatePDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        templatePDF = new TemplatePDF(getApplicationContext());
        templatePDF.openDocument();
        templatePDF.addMetaData("Clientes", "Ventas", "Sergey");
        templatePDF.addTitles("Tienda CodigoFacilito", "Clientes", "25/08/2018");
        templatePDF.addParagraph(shortText);
        templatePDF.addParagraph(longText);
        templatePDF.createTable(header, getClients());
        templatePDF.closeDocument();

    }
    public void pdfView(View view) {
        templatePDF.viewPDF();
    }
    public void pdfApp(View view) {
        templatePDF.appViewPDF(this);
    }
    private ArrayList<String[]>getClients(){
        ArrayList<String[]>rows = new ArrayList<>();
        rows.add(new String[]{"1","Pedro", "Lopez"});
        rows.add(new String[]{"2","Sofia", "Hernandez"});
        rows.add(new String[]{"3","Naomi", "Alfaro"});
        rows.add(new String[]{"4","Lorena", "Espejel"});
        return rows;
    }
}



