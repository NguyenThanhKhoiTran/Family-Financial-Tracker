import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

//Contributing Authors: Queszel Martin
public class MonthlyTotalModel {
    private final StringProperty month;
    private final DoubleProperty total;

    // Contributing Authors: Queszel Martin
    public MonthlyTotalModel(String month, double total) {
        this.month = new SimpleStringProperty(month);
        this.total = new SimpleDoubleProperty(total);
    }

    // Contributing Authors: Queszel Martin
    public StringProperty monthProperty() {
        return new SimpleStringProperty(month.get());
    }

    // Contributing Authors: Queszel Martin
    public DoubleProperty totalProperty() {
        return new SimpleDoubleProperty(total.get());
    }

    // Contributing Authors: Queszel Martin
    public String getMonth() {
        return month.get();
    }

    // Contributing Authors: Queszel Martin
    public double getTotal() {
        return total.get();
    }
}