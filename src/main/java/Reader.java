import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//Contributing authors: T Nguyen Thanh Khoi, D Tran Gia Khanh
public class Reader {

    // Contributing authors: D Tran Gia Khanh
    public static ArrayList<Transaction> readExcelFile(String filePath) {
        ArrayList<Transaction> dataList = new ArrayList<>();

        try {
            FileInputStream file = new FileInputStream(filePath);
            Workbook wb = new XSSFWorkbook(file);

            Sheet mySheet = wb.getSheetAt(0);

            for (int i = 0; i <= mySheet.getLastRowNum(); i++) {
                Row r = mySheet.getRow(i);

                if (r != null) {
                    String date = convertCellValueToString(r.getCell(0));
                    String desc = convertCellValueToString(r.getCell(1));
                    double credit = convertCellValueToDouble(r.getCell(2));
                    double debit = convertCellValueToDouble(r.getCell(3));
                    double balance = convertCellValueToDouble(r.getCell(4));

                    Transaction data = new Transaction(date, desc, credit, debit, balance);

                    dataList.add(data);
                }
            }
            wb.close();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public static ArrayList<Transaction> readCSVFile(String filePath) {
        ArrayList<Transaction> dataList = new ArrayList<>();

        try (Scanner sc = new Scanner(new File(filePath))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] values = line.split(",");

                String date = values.length > 0 ? values[0] : "0";
                String desc = values.length > 1 ? values[1] : "";
                double credit = 0.0;
                double debit = 0.0;

                if (values.length > 2 && !values[2].isEmpty()) {
                    try {
                        credit = Double.parseDouble(values[2]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid credit value, defaulting to 0.0: " + e.getMessage());
                    }
                }

                if (values.length > 3 && !values[3].isEmpty()) {
                    try {
                        debit = Double.parseDouble(values[3]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid debit value, defaulting to 0.0: " + e.getMessage());
                    }
                }

                double balance = values.length > 4 ? Double.parseDouble(values[4]) : 0.0;

                Transaction data = new Transaction(date, desc, credit, debit, balance);

                dataList.add(data);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public static String determineFileType(String filePath) {
        if (filePath.endsWith(".xlsx")) {
            return "EXCEL";
        } else if (filePath.endsWith(".csv")) {
            return "CSV";
        } else {
            return "UNKNOWN";
        }
    }

    public static String convertCellValueToString(Cell c) {
        if (c == null) {
            return "0";
        }
        switch (c.getCellType()) {
            case STRING:
                return c.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(c)) {
                    Date date = c.getDateCellValue();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    return formatter.format(date);
                } else {
                    return String.valueOf(c.getNumericCellValue());
                }
            default:
                return "0";
        }
    }

    // Contributing authors: T Nguyen Thanh Khoi, D Tran Gia Khanh
    public static double convertCellValueToDouble(Cell cellIn) {
        if (cellIn == null) {
            return 0.0;
        } else if (cellIn.getCellType() != CellType.NUMERIC) {
            throw new IllegalArgumentException("Invalid cell type or null cell");
        } else {
            return cellIn.getNumericCellValue();
        }
    }
}
