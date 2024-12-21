import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

// Contributing authors: T Nguyen Thanh Khoi
public class ReaderTest {

    // Contributing authors: T Nguyen Thanh Khoi
    @Test
    public void testDetermineFileType_ExcelFile() {
        String filePath = "2023-11.xlsx";
        assertEquals("EXCEL", Reader.determineFileType(filePath));
    }

    // Contributing authors: T Nguyen Thanh Khoi
    @Test
    public void testDetermineFileType_CsvFile() {
        String filePath = "2024-07.csv";
        assertEquals("CSV", Reader.determineFileType(filePath));
    }

    // Contributing authors: T Nguyen Thanh Khoi
    @Test
    public void testDetermineFileType_UnknownFile() {
        String filePath = "test.txt";
        assertEquals("UNKNOWN", Reader.determineFileType(filePath));
    }

    // Contributing authors: T Nguyen Thanh Khoi
    @Test
    public void testReadExcelFile() {
        String filePath = "src/tests/java/resources/2023-11.xlsx";

        List<Transaction> transactions = Reader.readExcelFile(filePath);

        assertEquals("2023-11-21", transactions.get(0).getDate());
        assertEquals("SUNSHINE FOODS LTD", transactions.get(0).getDescription());
        assertEquals(0.0, transactions.get(0).getCredit());
        assertEquals(18.76, transactions.get(0).getDebit());
        assertEquals(10312.37, transactions.get(0).getBalance());
    }

    // Contributing authors: T Nguyen Thanh Khoi
    @Test
    public void testReadCSVFile() {
        String filePath = "src/tests/java/resources/2024-07.csv";

        List<Transaction> transactions = Reader.readCSVFile(filePath);
        assertEquals("07/21/2024", transactions.get(0).getDate());
        assertEquals("IC* INSTACART", transactions.get(0).getDescription());
        assertEquals(0.0, transactions.get(0).getCredit());
        assertEquals(249.80, transactions.get(0).getDebit());
        assertEquals(5620.11, transactions.get(0).getBalance());
    }

    // Contributing authors: T Nguyen Thanh Khoi
    @Test
    public void testReadExcelFile_InvalidFile() {
        String filePath = "src/tests/java/resources/invalid.xlsx";

        List<Transaction> transactions = Reader.readExcelFile(filePath);

        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    // Contributing authors: T Nguyen Thanh Khoi
    @Test
    public void testReadCSVFile_InvalidFile() {
        String filePath = "src/tests/java/resources/invalid.csv";

        List<Transaction> transactions = Reader.readCSVFile(filePath);

        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());
    }

    // Contributing authors: T Nguyen Thanh Khoi
    @Test
    public void testConvertCellValueToString_NullCell() {
        Cell nullCell = null;
        String result = Reader.convertCellValueToString(nullCell);
        assertEquals("0", result);
    }

    // Contributing authors: T Nguyen Thanh Khoi
    @Test
    public void testConvertCellValueToString_StringCell() {
        @SuppressWarnings("resource")
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();
        Row r = sheet.createRow(0);
        Cell stringCell = r.createCell(0);
        stringCell.setCellValue("Test String");

        String result = Reader.convertCellValueToString(stringCell);
        assertEquals("Test String", result);
    }

    // Contributing authors: T Nguyen Thanh Khoi
    @Test
    public void testConvertCellValueToString_NumericCell() {
        @SuppressWarnings("resource")
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();
        Row r = sheet.createRow(0);
        Cell numericCell = r.createCell(1);
        numericCell.setCellValue(123.45);

        String result = Reader.convertCellValueToString(numericCell);
        assertEquals("123.45", result);
    }
}