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









        //        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOCUMENTS), "pdfdemo");
//        if (!pdfFolder.exists()) {
//            pdfFolder.mkdir();
//            Log.i(LOG_TAG, "Pdf Directory created");
//        }

//        Document document = new Document();
//        String outpath = Environment.getExternalStorageDirectory()+"/storage/emulated/0/Documents/helloworld.pdf";
//        try {
//            Toast toast = Toast.makeText(getApplicationContext(),
//                    "Файл создан", Toast.LENGTH_SHORT);
//            toast.show();
//            PdfWriter.getInstance(document,
//                    new FileOutputStream(outpath));
//            document.open();
//            document.add(new Paragraph("A Hello World PDF document."));
//            document.close(); // no need to close PDFwriter?
//            PdfPTable t = new PdfPTable(3);
//            t.setSpacingBefore(25);
//            t.setSpacingAfter(25);
//            PdfPCell c1 = new PdfPCell(new Phrase("Header1"));
//            t.addCell(c1);
//            PdfPCell c2 = new PdfPCell(new Phrase("Header2"));
//            t.addCell(c2);
//            PdfPCell c3 = new PdfPCell(new Phrase("Header3"));
//            t.addCell(c3);
//            t.addCell("1.1");
//            t.addCell("1.2");
//            t.addCell("1.3");

//        } catch (DocumentException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
}



