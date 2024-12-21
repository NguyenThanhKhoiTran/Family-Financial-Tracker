import java.util.ArrayList;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

//Contributing Authors: Queszel Martin, T Nguyen Thanh Khoi
public class TransactionModel {

    private StringProperty transDate;
    private StringProperty description;
    private DoubleProperty debit;
    private DoubleProperty credit;
    private DoubleProperty value;
    private DoubleProperty balance;
    private StringProperty category;
    private StringProperty buyer;
    private DoubleProperty total;

    // Contributing Authors: Queszel Martin
    public TransactionModel(Transaction transactionIn) {
        this.transDate = new SimpleStringProperty(transactionIn.getDate());
        this.description = new SimpleStringProperty(transactionIn.getDescription());
        this.debit = new SimpleDoubleProperty(transactionIn.getDebit());
        this.credit = new SimpleDoubleProperty(transactionIn.getCredit());
        this.value = new SimpleDoubleProperty(transactionIn.getValue());
        this.balance = new SimpleDoubleProperty(transactionIn.getBalance());
        this.category = new SimpleStringProperty(transactionIn.getCategory());
        this.buyer = new SimpleStringProperty(transactionIn.getBuyer());
        this.total = new SimpleDoubleProperty(transactionIn.getTotal());
    }

    // Contributing Authors: Queszel Martin
    public StringProperty transDateProperty() {
        return transDate;
    }

    // Contributing Authors: Queszel Martin
    public StringProperty descriptionProperty() {
        return description;
    }

    // Contributing Authors: Queszel Martin
    public DoubleProperty debitProperty() {
        return debit;
    }

    // Contributing Authors: Queszel Martin
    public DoubleProperty totalProperty() {
        return total;
    }

    // Contributing Authors: Queszel Martin
    public DoubleProperty creditProperty() {
        return credit;
    }

    // Contributing Authors: Queszel Martin
    public DoubleProperty valueProperty() {
        return value;
    }

    // Contributing Authors: Queszel Martin
    public DoubleProperty balanceProperty() {
        return balance;
    }

    // Contributing Authors: Queszel Martin
    public StringProperty categoryProperty() {
        return category;
    }

    // Contributing Authors: Queszel Martin
    public StringProperty buyerProperty() {
        return buyer;
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public void setCategory(String category) {
        this.category.set(category);
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public void setBuyer(String buyer) {
        this.buyer.set(buyer);
    }

    // Contributing Authors: Queszel Martin
    public void updateTransaction(Transaction transaction) {
        transaction.setDate(transDate.get());
        transaction.setDescription(description.get());
        transaction.setDebit(debit.get());
        transaction.setCredit(credit.get());
        transaction.setValue(value.get());
        transaction.setBalance(balance.get());
        transaction.setCategory(category.get());
        transaction.setBuyer(buyer.get());
    }

    // Contributing Authors: Queszel Martin, Gen AI
    public static ArrayList<TransactionModel> convertToModelList(ArrayList<Transaction> transactionList) {
        ArrayList<TransactionModel> transactionModelList = new ArrayList<>();
        for (Transaction t : transactionList) {
            TransactionModel model = new TransactionModel(t);
            transactionModelList.add(model);
        }
        return transactionModelList;
    }

    // Contributing Authors: Queszel Martin
    public static ArrayList<Transaction> convertToTransactionList(
            ObservableList<TransactionModel> transactionModelList) {
        ArrayList<Transaction> transactionList = new ArrayList<>();
        for (TransactionModel model : transactionModelList) {
            Transaction t = new Transaction(
                    model.transDateProperty().get(),
                    model.descriptionProperty().get(),
                    model.debitProperty().get(),
                    model.creditProperty().get(),
                    model.balanceProperty().get(),
                    model.categoryProperty().get(),
                    model.buyerProperty().get());
            transactionList.add(t);
        }
        return transactionList;
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public static Transaction convertToTransaction(TransactionModel model) {
        return new Transaction(
                model.transDateProperty().get(),
                model.descriptionProperty().get(),
                model.debitProperty().get(),
                model.creditProperty().get(),
                model.balanceProperty().get(),
                model.categoryProperty().get(),
                model.buyerProperty().get());
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public static ArrayList<Transaction> convertToModelList(ObservableList<TransactionModel> transactionModelList) {
        ArrayList<Transaction> transactionList = new ArrayList<>();
        for (TransactionModel model : transactionModelList) {
            Transaction t = new Transaction(
                    model.transDateProperty().get(),
                    model.descriptionProperty().get(),
                    model.debitProperty().get(),
                    model.creditProperty().get(),
                    model.balanceProperty().get(),
                    model.categoryProperty().get(),
                    model.buyerProperty().get());
            transactionList.add(t);
        }
        return transactionList;
    }
    
    // Contributing Authors: Queszel Martin, T Nguyen Thanh Khoi
    @Override
    public String toString() {
        String out = "";
        out += String.format("%-12s %-30s %-10.2f %-10.2f %-10.2f %-20s %-10s", transDate.get(), description.get(),
                debit.get(), credit.get(),
                balance.get(), category.get(), buyer.get());
        return out;
    }
}