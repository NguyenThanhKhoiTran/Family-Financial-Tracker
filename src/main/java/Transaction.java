import java.util.ArrayList;

// Contributing authors: T Nguyen Thanh Khoi, Queszel Martin
public class Transaction {
    private String transDate;
    private String description;
    private double debit;
    private double credit;
    private double value;
    private double balance;
    private String category;
    private String buyer;
    private final double total;

    // Contributing authors: T Nguyen Thanh Khoi, Queszel Martin
    public Transaction(String transDate, String description, double debit, double credit, double balance) {
        this.transDate = transDate;
        this.description = description;
        this.debit = debit;
        this.credit = credit;
        this.balance = balance;
        this.value = debit > 0 ? debit : -credit;
        this.total = debit - credit;
    }

    // Contributing authors: T Nguyen Thanh Khoi, Queszel Martin
    public Transaction(String transDate, String description, double debit, double credit, double balance,
            String category) {
        this.transDate = transDate;
        this.description = description;
        this.debit = debit;
        this.credit = credit;
        this.value = debit > 0 ? debit : -credit;
        this.balance = balance;
        this.category = category;
        this.total = debit - credit;
    }

    // Contributing authors: T Nguyen Thanh Khoi, Queszel Martin
    public Transaction(String transDate, String description, double debit, double credit, double balance,
            String category,
            String buyer) {
        this.transDate = transDate;
        this.description = description;
        this.debit = debit;
        this.credit = credit;
        this.value = debit > 0 ? debit : -credit;
        this.balance = balance;
        this.category = category;
        this.buyer = buyer;
        this.total = debit - credit;
    }

    // Contributing authors: Queszel Martin
    public double getTotal() {
        return total;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public String getDate() {
        return transDate;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public int getMonthDate() {
        int month = 0;
        if (transDate.contains("/")) {
            String[] date = transDate.split("/");
            month = Integer.parseInt(date[0]);
        } else {
            String[] date = transDate.split("-");
            month = Integer.parseInt(date[1]);
        }
        return month;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public int getYearDate() {
        int year = 0;
        if (transDate.contains("/")) {
            String[] date = transDate.split("/");
            year = Integer.parseInt(date[2]);
        } else {
            String[] date = transDate.split("-");
            year = Integer.parseInt(date[0]);
        }
        return year;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public String getDescription() {
        return description;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public double getDebit() {
        return debit;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public double getCredit() {
        return credit;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public double getBalance() {
        return balance;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public String getCategory() {
        return category;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public String getBuyer() {
        return buyer;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public double getValue() {
        return value;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public void setCategory(String category) {
        this.category = category;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public void setDate(String dateIn) {
        this.transDate = dateIn;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public void setDescription(String descIn) {
        this.description = descIn;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public void setDebit(double debitIn) {
        this.debit = debitIn;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public void setCredit(double creditIn) {
        this.credit = creditIn;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public void setValue(double valIn) {
        this.value = valIn;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public void setBalance(double balanceIn) {
        this.balance = balanceIn;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    public static ArrayList<TransactionModel> convertToModelList(ArrayList<Transaction> transactionList) {
        ArrayList<TransactionModel> transactionModelList = new ArrayList<>();
        for (Transaction t : transactionList) {
            transactionModelList.add(new TransactionModel(t));
        }
        return transactionModelList;
    }

    // Contributing authors: T Nguyen Thanh Khoi
    @Override
    public String toString() {
        String out = "";
        out += String.format("%-12s %-30s %-10.2f %-10.2f %-10.2f %-20s %-10s", transDate, description, debit, credit,
                balance, category, buyer);
        return out;
    }
}
