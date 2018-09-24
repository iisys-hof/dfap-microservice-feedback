/*
 * Copyright 2018 Thomas Winkler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xlsConverter;

import daos.ordering.JPAOrderingDao;
import entities.FeedbackEntry;
import entities.Ordering;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

@SuppressWarnings("Duplicates")
@RequestScoped
public class FeedbackXLSConverter {

    public static void main(String[] args) {
      FeedbackXLSConverter x =   new FeedbackXLSConverter();
      x.generatetemplate();
      //x.getData(0);
      x.save();
    }

    @Inject
    JPAOrderingDao orderDao;
    Ordering order;


    private final String FILE_NAME = "/tmp/MyFirstExcel.xlsx";
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private int rowNum = 0;
    private CellStyle styleMedium;

    private boolean go = true;

    private List<FeedbackEntry> feedbackJsonList;

    private Cell dateCell, orderCell, snrCell, nameCell, articleCell, toolCell;

    SimpleDateFormat timeFormat = new SimpleDateFormat("kk :mm");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");


    public void getData(long orderId) {

        order = orderDao.readOrdering(orderId);
        feedbackJsonList = order.getFeedbackEntryList();
        this.fillInData();
    }
    public void getData(List<FeedbackEntry> feedbacks) {

        feedbackJsonList = feedbacks;
        this.fillInData();
    }

    private void fillInGapRow() {
        int colNum = 0;
        Row row = sheet.createRow(rowNum++);
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
        row.createCell(colNum++).setCellValue(" ");
    }

    public void generatetemplate() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Datatypes in Java");


        styleMedium = workbook.createCellStyle();
        styleMedium.setBorderBottom(BorderStyle.THIN);
        styleMedium.setBorderLeft(BorderStyle.THIN);
        styleMedium.setBorderRight(BorderStyle.THIN);
        styleMedium.setBorderTop(BorderStyle.THIN);
        styleMedium.setAlignment(HorizontalAlignment.CENTER);

        CellStyle headLine = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short)24);
        headLine.setFont(font);

        CellStyle styleBold = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        styleBold.setFont(boldFont);
        styleBold.setBorderBottom(BorderStyle.MEDIUM);
        styleBold.setBorderLeft(BorderStyle.MEDIUM);
        styleBold.setBorderRight(BorderStyle.MEDIUM);
        styleBold.setBorderTop(BorderStyle.MEDIUM);
        styleBold.setAlignment(HorizontalAlignment.CENTER);
        styleBold.setVerticalAlignment(VerticalAlignment.JUSTIFY);


        System.out.println("Creating excel");

        int colNum1 = 0;
        Row row1 = sheet.createRow(rowNum++);
        row1.createCell(colNum1++).setCellValue("Rückmeldebogen GT");
        row1.getCell(0).setCellStyle(headLine);

        sheet.createRow(rowNum++);

        int colNum7 = 0;
        Row row7 = sheet.createRow(rowNum++);

        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum,colNum7,colNum7));
        row7.createCell(colNum7++).setCellValue("Stellplatz");

        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum,colNum7,colNum7));
        row7.createCell(colNum7++).setCellValue("Schicht");


        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum,colNum7,colNum7));
        row7.createCell(colNum7++).setCellValue("Auftrag");


        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum,colNum7,colNum7));
        row7.createCell(colNum7++).setCellValue("Startdatum");
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum,colNum7,colNum7));
        row7.createCell(colNum7++).setCellValue("Startzeit");

        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum,colNum7,colNum7));
        row7.createCell(colNum7++).setCellValue("Enddatum");
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum,colNum7,colNum7));
        row7.createCell(colNum7++).setCellValue("Endzeit");

        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum -1,colNum7,colNum7 + 2));
        row7.createCell(colNum7++).setCellValue("Menge");
        row7.createCell(colNum7++);
        row7.createCell(colNum7++);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum,colNum7,colNum7));
        row7.createCell(colNum7++).setCellValue("Gewicht");
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum,colNum7,colNum7));
        row7.createCell(colNum7++).setCellValue("Geschwindigkeit");
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum,colNum7,colNum7));
        row7.createCell(colNum7++).setCellValue("Vorgang");

        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum,colNum7,colNum7));
        row7.createCell(colNum7++).setCellValue("Artikel");
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum,colNum7,colNum7));
        row7.createCell(colNum7++).setCellValue("Werkzeug");

        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1,rowNum,colNum7,colNum7));
        row7.createCell(colNum7++).setCellValue("Personalnummer");




        int colNum8 = 0;
        Row row8 = sheet.createRow(rowNum++);
        row8.createCell(colNum8++);
        row8.createCell(colNum8++);
        row8.createCell(colNum8++);
        row8.createCell(colNum8++);
        row8.createCell(colNum8++);
        row8.createCell(colNum8++);
        row8.createCell(colNum8++);
        row8.createCell(colNum8++).setCellValue("Gutware");
        row8.createCell(colNum8++).setCellValue("Gesamt");
        row8.createCell(colNum8++).setCellValue("Ausschuss");
        row8.createCell(colNum8++);
        row8.createCell(colNum8++);
        row8.createCell(colNum8++);
        row8.createCell(colNum8++);
        row8.createCell(colNum8++);
        row8.createCell(colNum8++);



        designCells(styleBold, 2, 7,0, -1 );
        designCells(styleBold, 8, 10,0, -1 );
    }

    public double round(final double value, final int frac) {
        return Math.round(Math.pow(10.0, frac) * value) / Math.pow(10.0, frac);
    }
    
    public String roundAndFormat(final double value, final int frac) {
        final java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setMaximumFractionDigits(frac);
        return nf.format(new BigDecimal(value));
    }

        public void fillInData(){

        if(!go) {
            System.err.println("STOP FILL DATA");
            return;
        }
        System.out.println("FILL IN DATA");


        float accpetedSum = 0;

        // To calculate the right acceptedSum; and place a empty line
        long orderingId = 0;

        for (FeedbackEntry fb: feedbackJsonList) {

            System.out.println(orderingId);

            if (orderingId != fb.getOrderingId().getOrderingId()){
                accpetedSum = 0;
                orderingId = fb.getOrderingId().getOrderingId();
                Row row = sheet.createRow(rowNum++);

            }
            orderingId = fb.getOrderingId().getOrderingId();

            accpetedSum += fb.getAccepted();
            int colNum = 0;
            Row row = sheet.createRow(rowNum++);
            row.createCell(colNum++).setCellValue(fb.getOrderingId().getMachineId().getName());
            row.createCell(colNum++).setCellValue(fb.getShift());
            row.createCell(colNum++).setCellValue(fb.getOrderingId().getOrderingId());


            row.createCell(colNum++).setCellValue(dateFormat.format(fb.getStartTime()));
            row.createCell(colNum++).setCellValue(timeFormat.format(fb.getStartTime()));
            row.createCell(colNum++).setCellValue(dateFormat.format(fb.getEndTime()));
            row.createCell(colNum++).setCellValue(timeFormat.format(fb.getEndTime()));
            row.createCell(colNum++).setCellValue(round(fb.getAccepted(), 3));
            row.createCell(colNum++).setCellValue(round(accpetedSum, 3));
            row.createCell(colNum++).setCellValue(round(fb.getRejected(), 3));

            row.createCell(colNum++).setCellValue(round(fb.getWeight(), 3));
            row.createCell(colNum++).setCellValue(round(fb.getSpeed(),3));
            row.createCell(colNum++).setCellValue(fb.getSubProcessId().getAbbreviation());

            row.createCell(colNum++).setCellValue(fb.getOrderingId().getArticleId().getName());

            if(fb.getOrderingId().getToolId().getVersion() != null) {
                row.createCell(colNum++).setCellValue(fb.getOrderingId().getToolId().getName() + fb.getOrderingId().getToolId().getVersion());
            } else {
                row.createCell(colNum++).setCellValue(fb.getOrderingId().getToolId().getName());
            }

            if(fb.getEmployeeNumber() != null)
                row.createCell(colNum++).setCellValue(fb.getEmployeeNumber());
            else
                row.createCell(colNum++).setCellValue("-");

        }

        int rowEnd = Math.max(1400, sheet.getLastRowNum());
        designCells(styleMedium, 4, rowEnd,0, -1 );

        if(feedbackJsonList.size() > 0)
            this.fillInGapRow();
    }

    public void save() {
        for (int i = 1; i < 20; i++) {
            sheet.autoSizeColumn(i, true);
        }

        sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
        sheet.setFitToPage(true);
        sheet.getPrintSetup().setOrientation(PrintOrientation.LANDSCAPE);
        sheet.getPrintSetup().setFitWidth((short)1);
        sheet.getPrintSetup().setFitHeight((short)0);

        try {
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done");
    }

    public byte[] getByteArray() {
        if(!go) {
            System.err.println("STOP SEND FILE");
            return null;
        }
        for (int i = 1; i < 20; i++) {
            sheet.autoSizeColumn(i, true);
        }

        sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
        sheet.setFitToPage(true);
        sheet.getPrintSetup().setOrientation(PrintOrientation.LANDSCAPE);
        sheet.getPrintSetup().setFitWidth((short)1);
        sheet.getPrintSetup().setFitHeight((short)0);

        sheet.createFreezePane(0,4);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
           workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();

    }
    public String getFileName() {
        return order.getOrderingId() + "-" + dateFormat.format(order.getDate()) + ".xlsx";
    }

    public int send() {
        if(!go) {
            System.err.println("STOP SEND FILE");
            return 500;
        }
        for (int i = 1; i < 20; i++) {
            sheet.autoSizeColumn(i, true);
        }

        sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
        sheet.setFitToPage(true);
        sheet.getPrintSetup().setOrientation(PrintOrientation.LANDSCAPE);
        sheet.getPrintSetup().setFitWidth((short)1);
        sheet.getPrintSetup().setFitHeight((short)0);


        Client client = ClientBuilder.newBuilder().newClient();
        WebTarget target = client.target("http://localhost:8080/dfap-document/file/feedback/");
        Invocation.Builder builder = target.request();


        String fileName = order.getOrderingId() + "-" + dateFormat.format(order.getDate());
        Response response = target.path(fileName).request().put(Entity.entity(this.getBase64(), MediaType.TEXT_PLAIN));
        return response.getStatus();
    }

    public String getBase64() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
       // Base64.encodeBase64String
        byte[] bytes = outputStream.toByteArray();
        return Base64.encodeBase64String(bytes);
    }

    private  void designCells(CellStyle style, int rowStart, int rowEnd, int cellStart, int cellStop) {

        for (int row = rowStart; row < rowEnd; row++) {
            Row r = this.sheet.getRow(row);
            if (r == null) {
                continue;
            }

            int lastColumn = r.getLastCellNum();
            if(cellStop == -1) {
                for (int cn = cellStart; cn < lastColumn; cn++) {
                    Cell c = r.getCell(cn, CREATE_NULL_AS_BLANK);
                    c.setCellStyle(style);

                }
            } else {
                for (int cn = cellStart; cn < cellStop; cn++) {
                    Cell c = r.getCell(cn, CREATE_NULL_AS_BLANK);
                    c.setCellStyle(style);

                }
            }
        }
    }


}
