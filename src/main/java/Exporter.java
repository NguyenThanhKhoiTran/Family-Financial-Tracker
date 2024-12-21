import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.collections.ObservableList;

//Contributing authors: T Nguyen Thanh Khoi
public class Exporter {
    // Contributing authors: T Nguyen Thanh Khoi
    public void exportToCSV(List<Transaction> transactions, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("Date,Description,Value,Buyer,Category");

            for (Transaction transaction : transactions) {
                writer.printf("%s,%s,%s,%.2f,%s%n",
                        transaction.getDate(),
                        transaction.getDescription(),
                        transaction.getBuyer(),
                        transaction.getValue(),
                        transaction.getCategory());
            }
            System.out.println("Data successfully exported to: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public void exportToXLSX(List<Transaction> transactions, String fileName) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet newSheet = wb.createSheet("Transactions");

            Row header = newSheet.createRow(0);
            header.createCell(0).setCellValue("Date");
            header.createCell(1).setCellValue("Description");
            header.createCell(2).setCellValue("Buyer");
            header.createCell(3).setCellValue("Value");
            header.createCell(4).setCellValue("Category");

            int rowNum = 1;
            for (Transaction transaction : transactions) {
                Row r = newSheet.createRow(rowNum++);
                r.createCell(0).setCellValue(transaction.getDate());
                r.createCell(1).setCellValue(transaction.getDescription());
                r.createCell(2).setCellValue(transaction.getBuyer());
                r.createCell(3).setCellValue(transaction.getValue());
                r.createCell(4).setCellValue(transaction.getCategory());
            }
            newSheet.autoSizeColumn(0);
            newSheet.autoSizeColumn(1);
            newSheet.autoSizeColumn(2);
            newSheet.autoSizeColumn(3);
            newSheet.autoSizeColumn(4);

            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                wb.write(fileOut);
                System.out.println("Data successfully exported to: " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public List<String[]> summarizeTransactions(List<Transaction> transactions) {
        Set<String> buyers = new HashSet<>();
        for (Transaction t : transactions) {
            buyers.add(t.getBuyer());
        }
        List<String> buyerList = new ArrayList<>(buyers);

        Map<String, double[]> summaryMap = new LinkedHashMap<>();
        int buyerCount = buyerList.size();
        double[] overallTotals = new double[buyerCount + 1];

        for (Transaction t : transactions) {
            String category = t.getCategory();
            String buyer = t.getBuyer();
            double value = t.getValue();

            summaryMap.putIfAbsent(category, new double[buyerCount + 1]);

            int buyerIndex = buyerList.indexOf(buyer);
            summaryMap.get(category)[buyerIndex] += value;
            summaryMap.get(category)[buyerCount] += value;

            overallTotals[buyerIndex] += value;
            overallTotals[buyerCount] += value;
        }

        List<String[]> summarizedRows = new ArrayList<>();
        for (Map.Entry<String, double[]> entry : summaryMap.entrySet()) {
            String category = entry.getKey();
            double[] values = entry.getValue();

            String[] row = new String[buyerCount + 2];
            row[0] = category;
            for (int i = 0; i < values.length; i++) {
                row[i + 1] = String.format("%.2f", values[i]);
            }
            summarizedRows.add(row);
        }

        String[] header = new String[buyerCount + 2];
        header[0] = "Category";
        for (int i = 0; i < buyerCount; i++) {
            header[i + 1] = buyerList.get(i);
        }
        header[buyerCount + 1] = "Total";
        summarizedRows.add(0, header);

        String[] totalRow = new String[buyerCount + 2];
        totalRow[0] = "TOTAL SPENDING";
        for (int i = 0; i < overallTotals.length; i++) {
            totalRow[i + 1] = String.format("%.2f", overallTotals[i]);
        }
        summarizedRows.add(totalRow);

        return summarizedRows;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public void exportSummaryToXLSXDynamic(List<Transaction> transactions, String fileName) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet s = wb.createSheet("Summary");
            List<String[]> summarizedData = summarizeTransactions(transactions);

            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle totalStyle = wb.createCellStyle();
            totalStyle.setBorderBottom(BorderStyle.THIN);
            totalStyle.setBorderTop(BorderStyle.THICK);
            totalStyle.setAlignment(HorizontalAlignment.CENTER);
            totalStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Font f = wb.createFont();
            f.setBold(true);
            f.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(f);

            int rowNum = 0;
            for (String[] rowData : summarizedData) {
                Row r = s.createRow(rowNum++);

                if (rowNum == 1) {
                    for (int colNum = 0; colNum < rowData.length; colNum++) {
                        Cell c = r.createCell(colNum);
                        c.setCellValue(rowData[colNum]);
                        c.setCellStyle(headerStyle);
                    }
                } else {
                    for (int colNum = 0; colNum < rowData.length; colNum++) {
                        Cell c = r.createCell(colNum);
                        c.setCellValue(rowData[colNum]);
                    }
                }
            }

            Row lastRow = s.getRow(rowNum - 1);
            for (int colNum = 0; colNum < lastRow.getPhysicalNumberOfCells(); colNum++) {
                lastRow.getCell(colNum).setCellStyle(totalStyle);
            }

            for (int i = 0; i < summarizedData.get(0).length; i++) {
                s.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                wb.write(fileOut);
            }

            System.out.println("Data successfully exported to: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public void exportSummaryToCSVDynamic(List<Transaction> transactions, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            List<String[]> summarizedData = summarizeTransactions(transactions);
            for (String[] row : summarizedData) {
                writer.println(String.join(",", row));
            }

            System.out.println("Summary successfully exported to: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public void addSheetsForMonthlyTransactions(String filePath, List<Transaction> transactions) {
        try (FileInputStream fis = new FileInputStream(filePath);
                Workbook wb = new XSSFWorkbook(fis)) {

            if (wb.getSheetAt(0) != null) {
                wb.removeSheetAt(0);
            }
            Set<String> uniqueMonths = new HashSet<>();
            for (Transaction t : transactions) {
                String yearMonth = String.format("%04d-%02d", t.getYearDate(), t.getMonthDate());
                uniqueMonths.add(yearMonth);
            }

            for (String yearMonth : uniqueMonths) {
                if (wb.getSheet(yearMonth) == null) {
                    createSheetForMonth(wb, yearMonth, transactions);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                wb.write(fos);
            }

            System.out.println("Sheets successfully added to: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Contributing authors: T Nguyen Thanh Khoi
    private void createSheetForMonth(Workbook wb, String sheetName, List<Transaction> transactions) {
        Sheet newSheet = wb.createSheet(sheetName);

        String[] headers = { "Date", "Description", "BuyerID", "Amount", "Category" };
        Row headerRow = newSheet.createRow(0);
        CellStyle headerStyle = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int colNum = 0; colNum < headers.length; colNum++) {
            Cell c = headerRow.createCell(colNum);
            c.setCellValue(headers[colNum]);
            c.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        double totalAmount = 0.0;
        for (Transaction transaction : transactions) {
            String yearMonth = String.format("%04d-%02d", transaction.getYearDate(), transaction.getMonthDate());

            if (yearMonth.equals(sheetName)) {
                Row row = newSheet.createRow(rowNum++);
                row.createCell(0).setCellValue(transaction.getDate());
                row.createCell(1).setCellValue(transaction.getDescription());
                row.createCell(2).setCellValue(transaction.getBuyer());
                row.createCell(3).setCellValue(transaction.getValue());
                row.createCell(4).setCellValue(transaction.getCategory());
                totalAmount += transaction.getValue();
            }
        }
        Row totalRow = newSheet.createRow(rowNum);
        totalRow.createCell(0).setCellValue("TOTAL SPENDING");
        totalRow.createCell(3).setCellValue(totalAmount);

        CellStyle totalStyle = wb.createCellStyle();
        totalStyle.setBorderBottom(BorderStyle.THIN);
        totalStyle.setBorderTop(BorderStyle.THICK);
        totalStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        for (int i = 0; i < headers.length; i++) {
            Cell c = totalRow.getCell(i);
            if (c == null) {
                c = totalRow.createCell(i);
            }
            c.setCellStyle(totalStyle);
            newSheet.autoSizeColumn(i);
        }
    }

    // Contributing Authors: Queszel Martin
    public void copySheet(XSSFSheet sourceSheet, XSSFSheet targetSheet) {
        for (int i = 0; i < sourceSheet.getRow(0).getPhysicalNumberOfCells(); i++) {
            targetSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
        }
        for (int i = 0; i < sourceSheet.getPhysicalNumberOfRows(); i++) {
            Row sourceRow = sourceSheet.getRow(i);
            Row targetRow = targetSheet.createRow(i);
            if (sourceRow != null) {
                targetRow.setHeight(sourceRow.getHeight());
            }
            copyRow(sourceRow, targetRow);
        }
    }

    // Contributing Authors: Queszel Martin
    public void copyRow(Row sourceRow, Row targetRow) {
        if (sourceRow != null) {
            for (int i = 0; i < sourceRow.getPhysicalNumberOfCells(); i++) {
                Cell sourceCell = sourceRow.getCell(i);
                if (sourceCell != null) {
                    Cell targetCell = targetRow.createCell(i);
                    targetCell.setCellValue(sourceCell.toString());
                }
            }
        }
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public static void exportMultiBuyerBalanceSheet(ArrayList<Transaction> transactions,
            Map<String, Double> previousBalances, String outputFileName) throws Exception {

        Workbook wb = new XSSFWorkbook();
        Sheet s = wb.createSheet("Balance");

        Row buyerNameRow = s.createRow(0);
        Row columnHeaderRow = s.createRow(1);

        LinkedHashSet<String> buyers = new LinkedHashSet<>(previousBalances.keySet());
        int colIndex = 1;

        for (String buyer : buyers) {
            Cell buyerCell = buyerNameRow.createCell(colIndex);
            buyerCell.setCellValue(buyer + " Transactions");
            buyerCell.setCellStyle(createHeaderCellStyle(wb));
            s.setColumnWidth(colIndex, 3000);

            Cell hadPaidCell = columnHeaderRow.createCell(colIndex);
            hadPaidCell.setCellValue("Had Paid");
            hadPaidCell.setCellStyle(createHeaderCellStyle(wb));

            Cell purchasedCell = columnHeaderRow.createCell(colIndex + 1);
            purchasedCell.setCellValue("Purchased");
            purchasedCell.setCellStyle(createHeaderCellStyle(wb));
            s.setColumnWidth(colIndex + 1, 3000);

            Double previousBalance = previousBalances.getOrDefault(buyer, 0.0);
            Cell previousBalanceCell = buyerNameRow.createCell(colIndex + 2);
            previousBalanceCell.setCellValue(previousBalance);
            previousBalanceCell.setCellStyle(createHeaderCellStyle(wb));

            Cell curRunCell = columnHeaderRow.createCell(colIndex + 2);
            curRunCell.setCellValue("Cur Run");
            curRunCell.setCellStyle(createHeaderCellStyle(wb));
            s.setColumnWidth(colIndex + 2, 3000);
            s.addMergedRegion(new CellRangeAddress(0, 0, colIndex, colIndex + 1));
            colIndex += 4;
            s.setColumnWidth(colIndex, 2000);
        }
        String[] months = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
        HashMap<String, Double> curRunMap = new HashMap<>(previousBalances);

        for (int i = 0; i < months.length; i++) {
            Row r = s.createRow(i + 2);

            Cell monthCell = r.createCell(0);
            monthCell.setCellValue(months[i]);
            monthCell.setCellStyle(boldFont(wb));

            colIndex = 1;
            double totalHadPaid = 0.0;
            double totalPurchased = 0.0;

            for (String buyer : buyers) {
                if (buyer.equals("Total")) {
                    r.createCell(colIndex).setCellValue(totalHadPaid);
                    r.createCell(colIndex + 1).setCellValue(totalPurchased);
                    r.createCell(colIndex + 2)
                            .setCellValue(curRunMap.getOrDefault(buyer, 0.0) + totalPurchased - totalHadPaid);
                } else {
                    double totalCredit = calculateMonthlyTotal(transactions, buyer, i + 1, "credit");
                    double totalDebit = calculateMonthlyTotal(transactions, buyer, i + 1, "debit");
                    double curRun = curRunMap.getOrDefault(buyer, 0.0) + totalDebit - totalCredit;

                    r.createCell(colIndex).setCellValue(totalCredit);
                    r.createCell(colIndex + 1).setCellValue(totalDebit);
                    r.createCell(colIndex + 2).setCellValue(curRun);

                    curRunMap.put(buyer, curRun);
                    totalHadPaid += totalCredit;
                    totalPurchased += totalDebit;

                    colIndex += 3;
                    colIndex += 1;
                }
            }
        }
        Row totalRow = s.createRow(14);
        Cell totalCell = totalRow.createCell(0);
        totalCell.setCellValue("Total:");
        totalCell.setCellStyle(createHeaderCellStyle(wb));

        colIndex = 1;
        for (String buyer : buyers) {
            double totalCredit = calculateTotal(transactions, buyer, "credit");
            double totalDebit = calculateTotal(transactions, buyer, "debit");

            Cell totalCreditCell = totalRow.createCell(colIndex);
            totalCreditCell.setCellValue(totalCredit);
            totalCreditCell.setCellStyle(createHeaderCellStyle(wb));

            Cell totalDebitCell = totalRow.createCell(colIndex + 1);
            totalDebitCell.setCellValue(totalDebit);
            totalDebitCell.setCellStyle(createHeaderCellStyle(wb));

            Cell emptyCell = totalRow.createCell(colIndex + 2);
            emptyCell.setCellStyle(createHeaderCellStyle(wb));

            colIndex += 3;
            colIndex += 1;
        }

        try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
            wb.write(fos);
        }
        wb.close();
        System.out.println("Excel file created: " + outputFileName);
    }

    // Contributing authors: T Nguyen Thanh Khoi
    private static double calculateMonthlyTotal(ArrayList<Transaction> transactions, String buyer, int month,
            String type) {
        double total = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getBuyer().equals(buyer) && transaction.getMonthDate() == month) {
                total += type.equals("credit") ? transaction.getCredit() : transaction.getDebit();
            }
        }
        return total;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    private static double calculateTotal(ArrayList<Transaction> transactions, String buyer, String type) {
        double total = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getBuyer().equals(buyer)) {
                total += type.equals("credit") ? transaction.getCredit() : transaction.getDebit();
            }
        }
        return total;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    private static CellStyle createHeaderCellStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        style.setFont(f);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    private static CellStyle boldFont(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(f);
        return style;
    }

    // Contributing authors: D Tran Gia Khanh, T Nguyen Thanh Khoi
    public static void exportTxtFileListingCategoryFunction(ObservableList<TransactionModel> transactionModelsIn,
            String fileName) {
        Map<String, Double> categoryListPrice = new HashMap<>();
        Set<String> categoryListIn = new HashSet<>();
        for (TransactionModel transactionModel : transactionModelsIn) {
            categoryListIn.add(transactionModel.categoryProperty().get());
        }

        for (String category : categoryListIn) {
            categoryListPrice.put(category, 0.0);
        }
        ArrayList<Transaction> t = TransactionModel.convertToTransactionList(transactionModelsIn);
        for (Transaction transaction : t) {
            String containCheck = transaction.getCategory();
            if (categoryListPrice.containsKey(containCheck)) {
                double increase = categoryListPrice.get(containCheck);
                increase += transaction.getValue();
                categoryListPrice.put(containCheck, increase);
            }
        }

        File outputFile = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("Monthly Spending Trends:\n");
            double tol = 0.0;
            for (Map.Entry<String, Double> entry : categoryListPrice.entrySet()) {
                String category = entry.getKey();
                double value = entry.getValue();
                String formattedLine = String.format("%-20s %10.2f", category, value);
                writer.write(formattedLine);
                writer.newLine();
                tol += value;
            }
            writer.write("-------------------------------\n");
            writer.write("\t\tTotal: $" + String.format("%.2f", tol));
            System.out.println("File saved successfully at: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error writing to file.");
        }
    }
}