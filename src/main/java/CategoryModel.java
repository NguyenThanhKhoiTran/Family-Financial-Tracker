import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

// Contributing Authors: T Nguyen Thanh Khoi
public class CategoryModel {
    private StringProperty category;
    private ObservableMap<String, DoubleProperty> buyerTotals;
    private DoubleProperty totalCategory;

    // Contributing Authors: T Nguyen Thanh Khoi
    public CategoryModel(String category, ObservableList<String> uniqueBuyers) {
        this.category = new SimpleStringProperty(category);
        this.buyerTotals = FXCollections.observableHashMap();

        for (String buyer : uniqueBuyers) {
            this.buyerTotals.put(buyer, new SimpleDoubleProperty(0.0));
        }

        this.totalCategory = new SimpleDoubleProperty(0.0);
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public String getCategory() {
        return category.get();
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public void setCategory(String category) {
        this.category.set(category);
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public StringProperty categoryProperty() {
        return category;
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public DoubleProperty getTotalCategory() {
        return totalCategory;
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public void setTotalForBuyer(String buyer, double total) {
        if (buyerTotals.containsKey(buyer)) {
            buyerTotals.get(buyer).set(total);
        }
        calculateTotal();
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public DoubleProperty getTotalForBuyerProperty(String buyer) {
        return buyerTotals.get(buyer);
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public double getBuyerTotal(String buyer) {
        if (buyerTotals.containsKey(buyer)) {
            return buyerTotals.get(buyer).get();
        } else {
            return 0.0;
        }
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public void calculateTotal() {
        double sum = 0.0;
        for (DoubleProperty value : buyerTotals.values()) {
            sum += value.get();
        }
        sum = Math.round(sum * 100) / 100;
        this.totalCategory.set(sum);
    }
}
