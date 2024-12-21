import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
//Contributing Authors: Queszel
public class MonthlyTotalModelTest{
    //Contributing Authors: Queszel
    @Test
    void testConstructor(){
        MonthlyTotalModel model1 = new MonthlyTotalModel("May", 423.12);
        String monthProp = model1.monthProperty().get();
        assertEquals("May", monthProp);
    }
    //Contributing Authors: Queszel
    @Test
    void testTotalProperty(){
        MonthlyTotalModel model = new MonthlyTotalModel("May", 871.21);
        double monthTotal = model.totalProperty().get();
        assertEquals(871.21, monthTotal);
    }
    
}