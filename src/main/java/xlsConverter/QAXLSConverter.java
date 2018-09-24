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

import controller.QAController;
import daos.tool.JPAToolDao;
import entities.TestInstructionEntry;
import entities.TestInstructionProperty;
import entities.TestInstructionValue;
import entities.Tool;
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
import java.text.SimpleDateFormat;
import java.util.List;

@SuppressWarnings("Duplicates")
@RequestScoped
public class QAXLSConverter {

    @Inject
    JPAToolDao toolDao;

    @Inject
    QAController qaController;

    private Tool tool;


    SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private final String FILE_NAME = "/tmp/MyFirstExcel.xlsx";
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
   // private JsonNode valuesNode;
    private List<TestInstructionEntry> entries;
    private List<TestInstructionProperty> entryNames;

    private int offsetCol = 0;
    private int offsetRow = 3;

    private boolean go = true;

    public static void main(String[] args) {

        QAXLSConverter x = new QAXLSConverter();
        x.generateTemplate();
        x.getData(1);
        x.save();
    }

    public void generateTemplate() {
        System.out.println("Generating in Data");

        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Datatypes in Java");

        CellStyle styleThin;
        styleThin = workbook.createCellStyle();
        styleThin.setBorderBottom(BorderStyle.THIN);
        styleThin.setBorderLeft(BorderStyle.THIN);
        styleThin.setBorderRight(BorderStyle.THIN);
        styleThin.setBorderTop(BorderStyle.THIN);
        styleThin.setAlignment(HorizontalAlignment.CENTER);

        CellStyle styleBold = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        styleBold.setFont(boldFont);
        styleBold.setBorderBottom(BorderStyle.THIN);
        styleBold.setBorderLeft(BorderStyle.THIN);
        styleBold.setBorderRight(BorderStyle.THIN);
        styleBold.setBorderTop(BorderStyle.THIN);
        styleBold.setAlignment(HorizontalAlignment.CENTER);
        styleBold.setWrapText(true);


        CellStyle headLine = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short)24);
        headLine.setFont(font);

        CellStyle styleThinRightBorderBold = workbook.createCellStyle();
        styleThinRightBorderBold.setBorderBottom(BorderStyle.THIN);
        styleThinRightBorderBold.setBorderLeft(BorderStyle.THIN);
        styleThinRightBorderBold.setBorderRight(BorderStyle.MEDIUM);
        styleThinRightBorderBold.setBorderTop(BorderStyle.THIN);
        styleThinRightBorderBold.setAlignment(HorizontalAlignment.CENTER);
        styleThinRightBorderBold.setVerticalAlignment(VerticalAlignment.JUSTIFY);
        styleThinRightBorderBold.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        CellStyle styleBoldRightBorderBold = workbook.createCellStyle();
        boldFont.setBold(true);
        styleBoldRightBorderBold.setFont(boldFont);
        styleBoldRightBorderBold.setBorderBottom(BorderStyle.THIN);
        styleBoldRightBorderBold.setBorderLeft(BorderStyle.THIN);
        styleBoldRightBorderBold.setBorderRight(BorderStyle.MEDIUM);
        styleBoldRightBorderBold.setBorderTop(BorderStyle.THIN);
        styleBoldRightBorderBold.setAlignment(HorizontalAlignment.CENTER);
        styleBoldRightBorderBold.setVerticalAlignment(VerticalAlignment.JUSTIFY);

        System.out.println("Creating excel");


        Row row1 = sheet.createRow(0);
        row1.createCell(0).setCellValue("QS Rückmeldung");
        row1.getCell(0).setCellStyle(headLine);

        sheet.addMergedRegion(new CellRangeAddress(0,0,0,10));


        // Create 50 Rows
        for (int i = offsetRow; i <= 50 + offsetRow; i++) {
            Row row = sheet.createRow(i);
            if(i == offsetRow) {
                row.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));
            }
            // Create 26 Columns
            for (int j = offsetCol; j <= entries.size() + offsetCol + 1; j++) {

                Cell cell = row.createCell(j);
                cell.setCellStyle(styleThin);
                // Write Number in first Column
                if (j == 0 + offsetCol && i >= 1 + offsetRow) {
                    cell.setCellValue(i - offsetRow);
                    cell.setCellStyle(styleBold);
                }
                // Write Value name in
                if ( j == 1 + offsetCol && i + offsetRow != 0) {
                    cell.setCellStyle(styleBold);
                }
                // Write Number in first row
                if (i == 0 + offsetRow && j>=2 + offsetCol) {
                    cell.setCellValue(j - 1 - offsetCol);
                    cell.setCellStyle(styleBold);
                }
                // Set bold border every 4th column in first Row
                if ((j + 3 - offsetCol) % 4 == 0 ) {
                    cell.setCellStyle(styleBoldRightBorderBold);
                }
                // Set bold border every 4th column in first Row
                if ((j + 3 - offsetCol) % 4 == 0 && i > 0 + offsetRow) {
                    cell.setCellStyle(styleThinRightBorderBold);
                }

            }
        }
    }

    public void getData(long toolId) {
        /*System.out.println("Get Data");


       /* Client client = ClientBuilder.newBuilder().newClient();
        WebTarget target = client.target("http://localhost:8080/dfap-feedback/qs/" + toolId);
        Invocation.Builder builder = target.request();
        String x = builder.get(String.class);

        ObjectMapper mapper = new ObjectMapper();

        System.out.println("********************");
        String n = qaController.getArray(String.valueOf(toolId)).toString();
        System.out.println(n);

        try {
            valuesNode = mapper.readTree(n);
        } catch (Exception e) {
            System.err.println("NO QS VALUES FOUND FOR ID: " + toolId);
        }


        try {
            tool = this.toolDao.readToolForId(toolId);
        } catch (Exception e) {
            System.err.println("NO TOOL FOUND FOR ID: " + toolId);
        }

        this.fillInData();*/
    }
    public void getData(List<TestInstructionEntry> entries, List<TestInstructionProperty> entryNames) {
        this.entries = entries;
        this.entryNames = entryNames;
    }
    public void fillInDataMN() {
        System.out.println("Fill in Data");
            String heading =  sheet.getRow(0).getCell(0).getStringCellValue();
            //heading = heading + ": " + entries.get(0).getTestInstructionId().getToolId().getName();

            sheet.getRow(0).getCell(0).setCellValue(heading);

        for (TestInstructionProperty tip: this.entryNames) {
            Cell cell = sheet.getRow(tip.getNumber() + offsetRow).getCell(1 + offsetCol);
            if(tip.getName() != null && tip.getName().length() > 0) {
                cell.setCellValue(tip.getName());
            }
        }
        int i = 1;
        for (TestInstructionEntry entry: this.entries) {
            Cell cellH = sheet.getRow(offsetRow).getCell(1 + i + offsetCol);
            cellH.setCellValue(dateFormat.format(entry.getDate()) + "\n" + timeFormat.format(entry.getDate()) );



            for (TestInstructionValue value: entry.getTestInstructionValueList()) {
                Cell cell = sheet.getRow(value.getNumber() + offsetRow).getCell(1 + i + offsetCol);
                StringBuilder cv = new StringBuilder();
                if (value.getCheckValue()!= null) {
                    if(value.getCheckValue() == 0) {
                        cv.append("FEHLER");
                    } else {
                        cv.append("OK");
                    }
                    if (value.getCheckText()!= null && value.getCheckText().length() > 0) {
                        cv.append(" | ");
                    }
                }
                if (value.getCheckText()!= null) {
                    cv.append(value.getCheckText());
                }
                cell.setCellValue(cv.toString());
            }
            i++;
        }

    }


    public void save() {
        for (int i = 0; i < 26; i++) {
            //sheet.setColumnWidth(i, 900);
            sheet.autoSizeColumn(i);

        }

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
        for (int i = 0; i < 26; i++) {
            sheet.autoSizeColumn(i);

        }
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
        return  tool.getName() + "-" + tool.getToolId() + ".xlsx";
    }

    public int send() {
        System.out.println("Send Data");

        if(!go) {
            System.err.println("STOP SEND FILE");
            return 500;
        }
        for (int i = 0; i < 26; i++) {
            sheet.autoSizeColumn(i);

        }


        Client client = ClientBuilder.newBuilder().newClient();
        WebTarget target = client.target("http://localhost:8080/dfap-document/file/write/");
        Invocation.Builder builder = target.request();


        String fileName = "Qualitätssicherung/" + tool.getName() + tool.getToolId() + ".xlsx";
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
}


