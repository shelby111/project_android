package com.example.protokol.create_protokol;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.FontFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TemplatePDF {
    public static final String FONTBD = "res/font/timesbd.ttf";
    public static final String FONT = "res/font/times.ttf";
    private Context context;
    private File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private Font font = FontFactory.getFont(FONT, "Cp1251", BaseFont.EMBEDDED);
    private Font fontbd = FontFactory.getFont(FONTBD, "Cp1251", BaseFont.EMBEDDED);

    public TemplatePDF(Context context) {
        this.context = context;
    }

    public void openDocument() {
        createFile();
        try {
            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
        }catch (Exception e) {
            Log.e("openDocument", e.toString());
        }
    }

    private void createFile() {
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "PDF");
        if (!folder.exists())
             folder.mkdirs();
        Date currentTime = Calendar.getInstance().getTime();
        pdfFile = new File(folder, "TemplatePDF" + "(" + currentTime + ")" + ".pdf");
    }

    public void closeDocument() {
        document.close();
    }

    public void addMetaData(String title, String subject, String author){
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);
    }

    public void addTitles(String title, String subTitle) {
        try {
            paragraph = new Paragraph();
            addChildP(new Paragraph(title, fontbd));
            addChildP(new Paragraph(subTitle, fontbd));
            paragraph.setSpacingAfter(5);
            document.add(paragraph);
        }catch (Exception e) {
            Log.e("addTitles", e.toString());
        }
    }

    public void addCenter(String title) {
        try {
            paragraph = new Paragraph();
            addChildP(new Paragraph(title, fontbd));
            paragraph.setSpacingAfter(5);
            document.add(paragraph);
        }catch (Exception e) {
            Log.e("addCenter", e.toString());
        }
    }

    public void addZag(String zag) {
        try {
            paragraph = new Paragraph();
            addChildP(new Paragraph(zag, fontbd));
            paragraph.setSpacingAfter(5);
            document.add(paragraph);
        }catch (Exception e) {
            Log.e("addZag", e.toString());
        }
    }

    public void addChildP(Paragraph childParagraph) {
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }
    public void addParagraph(String text){
        try {
            paragraph = new Paragraph(text,font);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(5);
            document.add(paragraph);
        }catch (Exception e) {
            Log.e("addParagraph", e.toString());
        }
    }

    public void addRoom(String nameRoom, String number){
        try {
            int k = 1;
            paragraph = new Paragraph();
            paragraph.setFont(fontbd);
            PdfPTable pdfPTable = new PdfPTable(6);
            float[] columnWidths = new float[]{10f, 30f, 19f, 16f, 16f, 30f};
            pdfPTable.setWidths(columnWidths);
            pdfPTable.setWidthPercentage(100);
            PdfPCell pdfPCell;
            while (k <= 6) {
                pdfPCell = new PdfPCell(new Phrase(" ", font));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPTable.addCell(pdfPCell);
                k++;
            }
            paragraph.add(pdfPTable);
            document.add(paragraph);
            paragraph = new Paragraph();
            paragraph.setFont(fontbd);
            PdfPTable pdfPTable2 = new PdfPTable(1);
            pdfPTable2.setWidthPercentage(100);
            PdfPCell pdfPCell2;
            pdfPCell2 = new PdfPCell(new Phrase(number + nameRoom, fontbd));
            pdfPCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTable2.addCell(pdfPCell2);
            paragraph.add(pdfPTable2);
            document.add(paragraph);
            k=1;
            paragraph = new Paragraph();
            paragraph.setFont(fontbd);
            PdfPTable pdfPTable3 = new PdfPTable(6);
            pdfPTable3.setWidths(columnWidths);
            pdfPTable3.setWidthPercentage(100);
            PdfPCell pdfPCell3;
            while (k <= 6) {
                pdfPCell3 = new PdfPCell(new Phrase(" ", fontbd));
                pdfPCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPTable3.addCell(pdfPCell3);
                k++;
            }
            paragraph.add(pdfPTable3);
            document.add(paragraph);
        }catch (Exception e) {
            Log.e("addRoom", e.toString());
        }
    }

    public void addElement(String[]elements) {
        try {
            paragraph = new Paragraph();
            paragraph.setFont(font);
            PdfPTable pdfPTable = new PdfPTable(elements.length);
            float[] columnWidths = new float[]{10f, 30f, 19f, 16f, 16f, 30f};
            pdfPTable.setWidths(columnWidths);
            pdfPTable.setWidthPercentage(100);
            PdfPCell pdfPCell;
            int indexC = 0;
            while (indexC < elements.length) {
                pdfPCell = new PdfPCell(new Phrase(elements[indexC++], font));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPTable.addCell(pdfPCell);
            }
            paragraph.add(pdfPTable);
            document.add(paragraph);
        }catch (Exception e) {
            Log.e("addElement", e.toString());
        }
    }

    public void createTable(String[]header, ArrayList<String[]>clients) {
        try {
            paragraph = new Paragraph();
            paragraph.setFont(font);
            PdfPTable pdfPTable = new PdfPTable(header.length);
            float[] columnWidths = new float[]{10f, 30f, 19f, 16f, 16f, 30f};
            pdfPTable.setWidths(columnWidths);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setSpacingBefore(20);
            PdfPCell pdfPCell;
            int indexC = 0;
            while (indexC < header.length) {
                pdfPCell = new PdfPCell(new Phrase(header[indexC++], font));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPTable.addCell(pdfPCell);
            }
            for (int indexR = 0; indexR < clients.size(); indexR++) {
                String[]row = clients.get(indexR);
                for (indexC = 0; indexC < header.length; indexC++) {
                    pdfPCell = new PdfPCell(new Phrase(row[indexC], fontbd));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPTable.addCell(pdfPCell);
                }
            }
            paragraph.add(pdfPTable);
            document.add(paragraph);
        }catch (Exception e) {
            Log.e("createTable", e.toString());
        }
    }

    public void appViewPDF(Activity activity) {
        if (pdfFile.exists()) {
            Uri uri = Uri.fromFile(pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://deatails?id=com.adobe.reader")));
                Toast.makeText(activity.getApplicationContext(), "Не установлено приложение для чтения pdf", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(activity.getApplicationContext(), "Ошибка", Toast.LENGTH_LONG).show();
        }
    }
}
