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
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.FontFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    public void openDocument(String namefile, Boolean horizon) {
        createFile(namefile);
        try {
            if (!horizon)
                document = new Document(PageSize.A4);
            else
                document = new Document(PageSize.A4.rotate());
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
        }catch (Exception e) {
            Log.e("openDocument", e.toString());
        }
    }

    private void createFile(String namefile) {
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "PDF");
        if (!folder.exists())
             folder.mkdirs();
        pdfFile = new File(folder, namefile + ".pdf");
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
            paragraph.setSpacingBefore(7);
            paragraph.setSpacingAfter(5);
            document.add(paragraph);
        }catch (Exception e) {
            Log.e("addCenter", e.toString());
        }
    }

    public void addCenterNotBD(String title) {
        try {
            paragraph = new Paragraph();
            addChildP(new Paragraph(title, font));
            paragraph.setSpacingBefore(7);
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
            paragraph = new Paragraph();
            paragraph.setFont(fontbd);
            PdfPTable pdfPTable = new PdfPTable(6);
            float[] columnWidths = new float[]{10f, 30f, 19f, 16f, 16f, 30f};
            pdfPTable.setWidths(columnWidths);
            pdfPTable.setWidthPercentage(100);
            PdfPCell pdfPCell;
            int i;

            for (i = 1; i < 7; i++) {
                PdfPTable help = new PdfPTable(1);
                pdfPCell = new PdfPCell(new Phrase(String.valueOf(i), fontbd));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                help.addCell(pdfPCell);
                pdfPCell = new PdfPCell(new Phrase(" ", font));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                help.addCell(pdfPCell);
                pdfPCell = new PdfPCell(help);
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPTable.addCell(pdfPCell);
            }
            pdfPTable.setHeaderRows(1);
            pdfPTable.setSkipFirstHeader(true);

            for (i = 1; i < 14; i++) {
                if (i != 7) {
                    pdfPCell = new PdfPCell(new Phrase(" ", fontbd));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPTable.addCell(pdfPCell);
                }
                else {
                    pdfPCell = new PdfPCell(new Phrase(number + nameRoom, fontbd));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setColspan(6);
                    pdfPTable.addCell(pdfPCell);
                }
            }
            pdfPTable.setKeepTogether(true);
//            if (isSkipLastFooter()){
//                pdfPTable.deleteBodyRows();
//                for (i = 1; i < 7; i++) {
//                    PdfPTable help = new PdfPTable(1);
//                    pdfPCell = new PdfPCell(new Phrase(String.valueOf(i), fontbd));
//                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    help.addCell(pdfPCell);
//                    pdfPCell = new PdfPCell(new Phrase(" ", font));
//                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    help.addCell(pdfPCell);
//                    pdfPCell = new PdfPCell(help);
//                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    pdfPTable.addCell(pdfPCell);
//                }
//                for (i = 1; i < 14; i++) {
//                    if (i != 7) {
//                        pdfPCell = new PdfPCell(new Phrase(" ", fontbd));
//                        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                        pdfPTable.addCell(pdfPCell);
//                    }
//                    else {
//                        pdfPCell = new PdfPCell(new Phrase(number + nameRoom, fontbd));
//                        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                        pdfPCell.setColspan(6);
//                        pdfPTable.addCell(pdfPCell);
//                    }
//                }
//            }
            paragraph.add(pdfPTable);
            document.add(paragraph);
        }catch (Exception e) {
            Log.e("addRoom", e.toString());
        }
    }

    public void addElement(ArrayList<ArrayList> elements) {
        try {
            paragraph = new Paragraph();
            paragraph.setFont(font);
            PdfPTable pdfPTable = new PdfPTable(6);
            float[] columnWidths = new float[]{10f, 30f, 19f, 16f, 16f, 30f};
            pdfPTable.setWidths(columnWidths);
            pdfPTable.setWidthPercentage(100);
            PdfPCell pdfPCell;
            int i, j;
            for (i = 1; i < 7; i++) {
                PdfPTable help = new PdfPTable(1);
                pdfPCell = new PdfPCell(new Phrase(String.valueOf(i), fontbd));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                help.addCell(pdfPCell);
                pdfPCell = new PdfPCell(new Phrase(" ", font));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                help.addCell(pdfPCell);
                pdfPCell = new PdfPCell(help);
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPTable.addCell(pdfPCell);
            }
            pdfPTable.setHeaderRows(1);
            pdfPTable.setSkipFirstHeader(true);
            for (i = 0; i < elements.size(); i++) {
                for (j = 0; j < 6; j++) {
                    pdfPCell = new PdfPCell(new Phrase(String.valueOf(elements.get(i).get(j)), font));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPTable.addCell(pdfPCell);
                }
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

    public void createTableInsulation(String[]header) {
        try {
            paragraph = new Paragraph();
            paragraph.setFont(font);
            PdfPTable pdfPTable = new PdfPTable(9);
            float[] columnWidths = new float[]{3.2f, 15.4f, 4.2f, 5.3f, 6.7f, 4.2f, 5.4f, 47.7f, 6.7f};
            pdfPTable.setWidths(columnWidths);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setSpacingBefore(20);
            PdfPCell pdfPCell;
            int index = 0, i, j;
            for (i = 0; i < 9; ++i) {
                if (i == 0 || i == 1) {
                    pdfPCell = new PdfPCell(new Phrase(header[index], font));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    pdfPTable.addCell(pdfPCell);
                    index++;
                }
                if ((i > 1 && i < 7) || (i == 8)) {
                    pdfPCell = new PdfPCell(new Phrase(header[index], font));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    pdfPCell.setRotation(90);
                    pdfPTable.addCell(pdfPCell);
                    index++;
                }
                if (i == 7) {
                    PdfPTable r = new PdfPTable(10);
                    float[] columnWidths1 = new float[]{9.3f, 9.3f, 9.3f, 9.3f, 9.3f, 9.3f, 11.4f, 11.4f, 11.4f, 9.3f};
                    r.setWidths(columnWidths1);
                    pdfPCell = new PdfPCell(new Phrase(header[index], font));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    pdfPCell.setColspan(10);
                    r.addCell(pdfPCell);
                    index++;
                    for (j = 0; j < 10; j++) {
                        pdfPCell = new PdfPCell(new Phrase(header[index], font));
                        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        r.addCell(pdfPCell);
                        index++;
                    }
                    pdfPCell = new PdfPCell(r);
                    pdfPCell.setPadding(0);
                    pdfPTable.addCell(pdfPCell);
                }
            }
            index = 1;
            for (i = 0; i < 9; i++) {
                if (i < 7 || i == 8) {
                    pdfPCell = new PdfPCell(new Phrase(String.valueOf(index), fontbd));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    pdfPTable.addCell(pdfPCell);
                    index++;
                }
                else {
                    PdfPTable num = new PdfPTable(10);
                    float[] columnWidths1 = new float[]{9.3f, 9.3f, 9.3f, 9.3f, 9.3f, 9.3f, 11.4f, 11.4f, 11.4f, 9.3f};
                    num.setWidths(columnWidths1);
                    for (j = 0; j < 10; j++) {
                        pdfPCell = new PdfPCell(new Phrase(String.valueOf(index), fontbd));
                        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        num.addCell(pdfPCell);
                        index++;
                    }
                    pdfPCell = new PdfPCell(num);
                    pdfPCell.setPadding(0);
                    pdfPTable.addCell(pdfPCell);
                }
            }
            paragraph.add(pdfPTable);
            document.add(paragraph);
        }catch (Exception e) {
            Log.e("createTableInsulation", e.toString());
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
