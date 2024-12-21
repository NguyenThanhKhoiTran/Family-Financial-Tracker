import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

// Contributing authors: D Tran Gia Khanh
public class TestingMonthlyCalculate {

    // Contributing authors: D Tran Gia Khanh
    @Test
    public void testCalculateTotalMonthlyDebit() {

        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("11/20/2024", "Sample 1", 100, 0.0, 200, "Categ1", "Buyer1"));
        transactions.add(new Transaction("11/20/2024", "Sample 2", 100, 0.0, 300, "Categ1", "Buyer1"));
        transactions.add(new Transaction("11/20/2024", "Sample 3", 0, 20.5, 179.5, "Categ1", "Buyer5"));

        double result = TotalMonthlyByBuyer.calculateTotalMonthly(transactions, "Buyer1", true);

        assertEquals(200.0, result, 0.001);
    }

    // Contributing authors: D Tran Gia Khanh
    @Test
    public void testCalculateTotalMonthlyCredit() {

        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("11/20/2024", "Sample 1", 100, 0.0, 200, "Categ1", "Buyer1"));
        transactions.add(new Transaction("11/20/2024", "Sample 2", 100, 0.0, 300, "Categ1", "Buyer1"));
        transactions.add(new Transaction("11/20/2024", "Sample 3", 0, 20.5, 179.5, "Categ1", "Buyer5"));
        double result = TotalMonthlyByBuyer.calculateTotalMonthly(transactions, "Buyer1", false);
        double result2 = TotalMonthlyByBuyer.calculateTotalMonthly(transactions, "Buyer5", false);

        assertEquals(0.0, result, 0.001);
        assertEquals(20.5, result2, 0.001);
    }

    // Contributing authors: D Tran Gia Khanh
    @Test
    public void testCalculateTotalMonthlyForNonExistingBuyer() {

        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("11/20/2024", "Sample 1", 100, 0.0, 200, "Categ1", "Buyer1"));
        transactions.add(new Transaction("11/20/2024", "Sample 2", 100, 0.0, 300, "Categ1", "Buyer1"));
        transactions.add(new Transaction("11/20/2024", "Sample 3", 0, 20.5, 179.5, "Categ1", "Buyer5"));

        double result = TotalMonthlyByBuyer.calculateTotalMonthly(transactions, "Buyer3", true);
        double result2 = TotalMonthlyByBuyer.calculateTotalMonthly(transactions, "Buyer3", false);

        assertEquals(0.0, result, 0.001);
        assertEquals(0.0, result2, 0.001);
    }
}
