import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class TransactionTest 
{

    @Test
    public void testConstructorAndGetters() {
        Transaction t1 = new Transaction("2024-12-03", "Grocery Shopping", 50.0, 0.0, 500.0, "Groceries");

        assertEquals("2024-12-03", t1.getDate());

    }

    @Test
    public void testDateParsingWithDashSeparator() {
        Transaction t2 = new Transaction("2024-12-03", "Gas Bill", 0.0, 50.0, 350.0, "Bills");

        assertEquals(12, t2.getMonthDate());

    }

    @Test
    public void testDateParsingWithSlashSeparator() {
        Transaction t3 = new Transaction("12/03/2024", "Electric Bill", 100.0, 0.0, 400.0, "Bills");

        assertEquals(2024, t3.getYearDate());
    }

    @Test
    public void testSetters() {
        Transaction t5 = new Transaction("2024-12-03", "Miscellaneous", 0.0, 50.0, 450.0);

        t5.setCategory("Misc");
        t5.setBuyer("Alice");
        t5.setDebit(75.0);

        assertEquals(75.0, t5.getDebit());
    }
}
